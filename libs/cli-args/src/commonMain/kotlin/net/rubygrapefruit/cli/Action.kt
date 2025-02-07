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
        val parameter = DefaultActionParameter(actions.build())
        positional.add(parameter)
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
        val context = DefaultContext(DefaultHost, positional)
        return maybeParse(args, context)
    }

    internal fun state(context: ParseContext): ParseState {
        return ActionParseState(context, options, positional)
    }

    internal fun maybeParse(args: List<String>, parent: ParseContext): Result {
        val context = parent.withOptions(options)
        val actions = mutableListOf<() -> Unit>()
        val hints = mutableListOf<FailureHint>()

        var state = state(context)
        var index = 0
        while (index < args.size) {
            val current = args.subList(index, args.size)
            val result = state.parseNextValue(current)
            when (result) {
                is ParseState.Success -> {
                    result.collect(actions, hints)
                    var consumed = index + result.consumed
                    return if (consumed == args.size) {
                        actions.run()
                        Result.Success
                    } else {
                        // Should not need this but currently required by Recoverable
                        actions.run()
                        attemptToRecover(args, consumed, null, false, hints.merge(), DefaultHost, result.context ?: context)
                    }
                }

                is ParseState.Failure -> {
                    var recognized = index + result.recognized
                    return attemptToRecover(args, recognized, result.exception, result.expectedMore, hints.merge(), DefaultHost, context)
                }

                is ParseState.Nothing -> {
                    attemptToRecover(args, index, null, false, hints.merge(), DefaultHost, context)
                }

                is ParseState.Continue -> {
                    result.collect(actions, hints)
                    state = result.state
                    index += result.consumed
                }
            }
        }

        val result = state.endOfInput()
        return when (result) {
            is ParseState.FinishSuccess -> {
                actions.add(result.apply)
                actions.run()
                Result.Success
            }

            is ParseState.FinishFailure -> {
                attemptToRecover(args, args.size, result.exception, true, hints.merge(), DefaultHost, context)
            }
        }
    }

    private fun ParseState.Continue.collect(actions: MutableList<() -> Unit>, hints: MutableList<FailureHint>) {
        actions.add(apply)
        if (hint != null) {
            hints.add(hint)
        }
    }

    private fun ParseState.Success.collect(actions: MutableList<() -> Unit>, hints: MutableList<FailureHint>) {
        actions.add(apply)
        if (hint != null) {
            hints.add(hint)
        }
    }

    private fun List<() -> Unit>.run() {
        forEach { it() }
    }

    private fun List<FailureHint>.merge(): FailureHint? {
        return if (isEmpty()) {
            null
        } else if (size == 1) {
            first()
        } else {
            val hints = toList()
            object : FailureHint {
                override fun map(args: List<String>): ParseState.Failure? {
                    for (hint in hints) {
                        val failure = hint.map(args)
                        if (failure != null) {
                            return failure
                        }
                    }
                    return null
                }
            }
        }
    }

    private fun attemptToRecover(
        args: List<String>,
        recognized: Int,
        failure: ArgParseException?,
        expectedMore: Boolean,
        hint: FailureHint?,
        host: Host,
        parent: ParseContext
    ): Result {
        val context = parent.withOptions(options)

        // Allow an option to recover from parse failure by attempting to do a "recovery" parse from where parsing stopped
        var index = recognized
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

        if (hint != null && recognized < args.size) {
            val failure = hint.map(args.subList(recognized, args.size))
            if (failure != null) {
                return Result.Failure(failure.exception)
            }
        }

        // Determine the parse failure to report
        val arg = args.getOrNull(recognized)
        val originalFailure = failure
        val failure = when {
            // Parsing stopped on an unknown option
            arg != null && host.isOption(arg) -> {
                // Throw away the failure if parsing expected more but stopped on an unknown option
                val failure = if (originalFailure != null && expectedMore && !options.any { it.accepts(arg) }) {
                    null
                } else {
                    originalFailure
                }
                failure ?: ArgParseException("Unknown option: $arg")
            }

            // Parsing stopped due to a failure
            originalFailure != null -> originalFailure

            // Parsing stopped on a positional parameter
            else -> PositionalParseException("Unknown parameter: $arg", resolution = "Too many arguments provided: $arg", positional = context.positional)
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