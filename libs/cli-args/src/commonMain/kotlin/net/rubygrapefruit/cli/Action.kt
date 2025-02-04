package net.rubygrapefruit.cli

import kotlin.reflect.KClass

/**
 * An action that can be configured using command-line arguments.
 */
open class Action {
    private val options = mutableListOf<NonPositional>()
    private val positional = mutableListOf<Positional>()
    private val recoverables = mutableListOf<Recoverable>()

    /**
     * Allows parameters of type [String] to be added to this action.
     */
    fun string(): ConfigurationBuilder<String> {
        return DefaultConfigurationBuilder(this, DefaultHost, IdentityConverter)
    }

    /**
     * Allows parameters of type [Int] to be added to this action.
     */
    fun int(): ConfigurationBuilder<Int> {
        return DefaultConfigurationBuilder(this, DefaultHost, IntConverter)
    }

    /**
     * Allows parameters of type [Boolean] to be added to this action.
     */
    fun boolean(): BooleanConfigurationBuilder {
        return DefaultBooleanConfigurationBuilder(this, DefaultHost)
    }

    /**
     * Allows parameters of type [T] to be added to this action.
     */
    fun <T : Any> oneOf(type: KClass<T>, builder: Choices<T>.() -> Unit): MappingConfigurationBuilder<T> {
        val choices = DefaultChoices<T>(DefaultHost)
        builder(choices)
        val choicesByName = choices.choices.flatMap { it.names.map { name -> name to it } }.toMap()
        return DefaultMappingConfigurationBuilder(this, DefaultHost, ChoiceConverter(type, choicesByName), choices.choices)
    }

    /**
     * Allows parameters of type [T] to be added to this action.
     */
    inline fun <reified T : Any> oneOf(noinline builder: Choices<T>.() -> Unit): MappingConfigurationBuilder<T> {
        return oneOf(T::class, builder)
    }

    /**
     * Allows parameters of type [T] to be added to this action.
     * Uses the provided function to convert from string values to [T]
     */
    fun <T : Any> type(type: KClass<T>, converter: (String) -> ConversionResult<T>): ConfigurationBuilder<T> {
        return DefaultConfigurationBuilder(this, DefaultHost, MappingConverter(type, converter))
    }

    /**
     * Allows parameters of type [T] to be added to this action.
     * Uses the provided function to convert from string values to [T]
     */
    inline fun <reified T : Any> type(noinline converter: (String) -> ConversionResult<T>): ConfigurationBuilder<T> {
        return type(T::class, converter)
    }

    /**
     * Defines a string option with the given names. See [ConfigurationBuilder.option] for more details.
     */
    fun option(name: String, vararg names: String, help: String? = null): NullableOption<String> {
        return string().option(name, *names, help = help)
    }

    /**
     * Defines a boolean flag with the given names. See [BooleanConfigurationBuilder.flag] for more details.
     */
    fun flag(name: String, vararg names: String, help: String? = null): Flag {
        return boolean().flag(name, *names, help = help)
    }

    /**
     * Defines a string parameter with the given name. See [ConfigurationBuilder.parameter] for more details.
     */
    fun parameter(name: String, help: String? = null): RequiredParameter<String> {
        return string().parameter(name, help = help)
    }

    /**
     * Defines a list parameter that consumes the remainder of the command-line.
     */
    fun remainder(name: String, help: String? = null): OptionalListParameter<String> {
        DefaultHost.validate(name, "a parameter name")
        return add(RemainderParameter(name, help, false, this))
    }

    /**
     * Defines a set of actions. Use `<name> <action-args>` to invoke the action.
     *
     * Only one action can be invoked, and this must appear at a specific location in the input.
     * Fails if an action is not present in the input. Use [RequiredParameter.whenAbsent] to use a different default or [RequiredParameter.optional] to use a `null` value.
     */
    fun <T : Action> action(builder: Actions<T>.() -> Unit): Parameter<T> {
        val actions = DefaultActions<T>(DefaultHost)
        builder(actions)
        val parameter = DefaultActionParameter(actions.build(), DefaultHost)
        positional.add(parameter)
        options.add(parameter.option)
        recoverables.addAll(parameter.recoverables)
        return parameter
    }

    /**
     * Runs this action.
     */
    open fun run() {}

    /**
     * Configures this object from the given arguments, throwing an [ArgParseException] when the arguments could not be parsed.
     */
    @Throws(ArgParseException::class)
    fun parse(args: List<String>) {
        val result = maybeParse(args)
        if (result is Result.Failure) {
            throw result.failure
        }
    }

    /**
     * Configures this object from the given arguments.
     */
    fun maybeParse(args: List<String>): Result {
        val context = DefaultContext(positional, emptyList())
        val result = maybeParse(args, context)
        return if (result.recognized != args.size || result.failure != null) {
            attemptToRecover(args, result, DefaultHost, context)
        } else {
            Result.Success
        }
    }

    internal fun state(context: ParseContext, consumer: (Action) -> Unit): ParseState {
        return ActionParseState(context, this, options, positional, consumer)
    }

    internal fun maybeParse(args: List<String>, parent: ParseContext): ParseResult {
        val context = parent.withOptions(options)

        var state = state(context) {}
        var index = 0
        while (index < args.size) {
            val result = state.parseNextValue(args.subList(index, args.size))
            when (result) {
                is ParseState.Success -> {
                    result.apply()
                    return ParseResult.Success(index + result.consumed)
                }

                is ParseState.Failure -> {
                    return ParseResult.Failure(index + result.recognized, result.exception, false)
                }

                is ParseState.Nothing -> {
                    return ParseResult.Success(index)
                }

                is ParseState.Continue -> {
                    state = result.state
                    index += result.consumed
                }
            }
        }

        val result = state.endOfInput()
        return when (result) {
            is ParseState.FinishSuccess -> {
                result.apply()
                ParseResult.Success(args.size)
            }

            is ParseState.FinishFailure -> {
                ParseResult.Failure(args.size, result.exception, false)
            }
        }
    }

    private fun attemptToRecover(args: List<String>, original: ParseResult, host: Host, parent: ParseContext): Result {
        val context = parent.withOptions(options)

        // Allow an option to recover from parse failure by attempting to do a "recovery" parse from where parsing stopped
        var index = original.recognized
        while (index in args.indices) {
            val current = args.subList(index, args.size)
            for (option in recoverables) {
                val result = option.maybeRecover(current, context)
                if (result) {
                    // Don't attempt to keep parsing
                    return Result.Success
                }
            }
            // Skip this argument and attempt to recover on next argument
            index++
        }

        // Determine the parse failure to report
        val arg = args.getOrNull(original.recognized)
        val originalFailure = original.failure
        val failure = when {
            // Parsing stopped on an unknown option
            arg != null && host.isOption(arg) -> {
                // Throw away the failure if parsing expected more but stopped on an unknown option
                val failure = if (original is ParseResult.Failure && original.expectedMore && !context.options.any { it.accepts(arg) }) {
                    null
                } else {
                    originalFailure
                }
                failure ?: ArgParseException("Unknown option: $arg")
            }

            // Parsing stopped due to a failure
            originalFailure != null -> originalFailure

            // Parsing stopped on a positional parameter
            else -> ArgParseException("Unknown parameter: $arg")
        }
        return Result.Failure(failure)
    }

    fun usage(): ActionUsage {
        return ActionUsage(
            options.flatMap { it.usage() },
            positional.map { it.usage() }
        )
    }

    fun usage(name: String): ActionUsage? {
        return positional.firstOrNull()?.usage(name)
    }

    internal fun <T : NonPositional> add(option: T): T {
        options.add(option)
        return option
    }

    internal fun <T : Positional> add(param: T): T {
        positional.add(param)
        return param
    }

    internal fun <T : NonPositional> replace(option: NonPositional, newOption: T): T {
        options[options.indexOf(option)] = newOption
        return newOption
    }

    internal fun <T : Positional> replace(param: Positional, newParam: T): T {
        positional[positional.indexOf(param)] = newParam
        return newParam
    }

    internal fun positional(): List<Positional> = positional

    sealed class Result {
        data object Success : Result()
        data class Failure(val failure: ArgParseException) : Result()
    }

    sealed class ConversionResult<out T> {
        data class Success<T>(val value: T) : ConversionResult<T>()
        data class Failure<T>(val problem: String) : ConversionResult<T>()
    }

    interface Actions<T : Action> {
        /**
         * Adds an action selected using a name argument.
         */
        fun action(action: T, name: String, help: String? = null)

        /**
         * Adds an action selected using an option.
         */
        fun option(action: T, name: String, help: String? = null, allowAnywhere: Boolean = false)

        /**
         * Adds an action selected as a default.
         */
        fun action(action: T, help: String? = null)
    }

    interface Choices<T> {
        fun choice(value: T, name: String, vararg names: String, help: String? = null)
    }
}