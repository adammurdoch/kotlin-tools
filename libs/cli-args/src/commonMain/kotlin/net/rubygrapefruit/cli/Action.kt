package net.rubygrapefruit.cli

import kotlin.reflect.KClass

/**
 * An action that can be configured using command-line arguments.
 */
open class Action {
    private val options = mutableListOf<NonPositional>()
    private val positional = mutableListOf<Positional>()

    /**
     * Allows configuration values of type [String] to be added to this action.
     */
    fun string(): ConfigurationBuilder<String> {
        return DefaultConfigurationBuilder(this, DefaultHost, NoOpConverter)
    }

    /**
     * Allows configuration values of type [Int] to be added to this action.
     */
    fun int(): ConfigurationBuilder<Int> {
        return DefaultConfigurationBuilder(this, DefaultHost, IntConverter)
    }

    /**
     * Allows configuration values of type [Boolean] to be added to this action.
     */
    fun boolean(): BooleanConfigurationBuilder {
        return DefaultBooleanConfigurationBuilder(this, DefaultHost)
    }

    /**
     * Allows configuration values of type [T] to be added to this action.
     */
    fun <T : Any> oneOf(type: KClass<T>, builder: Choices<T>.() -> Unit): MappingConfigurationBuilder<T> {
        val choices = DefaultChoices<T>(DefaultHost)
        builder(choices)
        val choicesByName = choices.choices.flatMap { it.names.map { name -> name to it } }.toMap()
        return DefaultMappingConfigurationBuilder(this, DefaultHost, ChoiceConverter(type, choicesByName), choices.choices)
    }

    /**
     * Allows configuration values of type [T] to be added to this action.
     */
    inline fun <reified T : Any> oneOf(noinline builder: Choices<T>.() -> Unit): MappingConfigurationBuilder<T> {
        return oneOf(T::class, builder)
    }

    /**
     * Allows configuration values of type [T] to be added to this action.
     * Uses the provided function to convert from string values to [T]
     */
    fun <T : Any> type(type: KClass<T>, converter: (String) -> ConversionResult<T>): ConfigurationBuilder<T> {
        return DefaultConfigurationBuilder(this, DefaultHost, MappingConverter(type, converter))
    }

    /**
     * Allows configuration values of type [T] to be added to this action.
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
     * Defines a multi-value string parameter with the given name. See [ConfigurationBuilder.parameters] for more details.
     */
    fun parameters(name: String, help: String? = null): ListParameter<String> {
        return string().parameters(name, help = help)
    }

    /**
     * Defines a set of actions. Use `<name> <action-args>` to invoke the action.
     *
     * Only one action can be invoked, and this must appear at a specific location in the input.
     * Fails if an action is not present in the input. Use [RequiredParameter.whenAbsent] to use a different default or [RequiredParameter.optional] to use a `null` value.
     */
    fun <T : Action> actions(builder: Actions<T>.() -> Unit): RequiredParameter<T> {
        val actions = DefaultActions<T>(DefaultHost)
        builder(actions)
        val parameter = DefaultActionParameter(actions.build(), DefaultHost, this)
        positional.add(parameter)
        options.addAll(parameter.nonPositional)
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
        return if (result.count != args.size || result.failure != null) {
            attemptToRecover(args, result, DefaultHost, context)
        } else {
            Result.Success
        }
    }

    internal fun maybeParse(args: List<String>, parent: ParseContext): ParseResult {
        val pending = this.positional.toMutableList()
        val context = parent.withOptions(options)

        var index = 0
        var failure: ArgParseException? = null
        while (index in args.indices && failure == null) {
            val current = args.subList(index, args.size)
            var matched = false
            for (option in context.options) {
                val result = option.accept(current, context)
                if (result.count > 0 || result.failure != null) {
                    if (result.failure != null) {
                        failure = result.failure
                    }
                    index += result.count
                    matched = true
                    break
                }
            }
            if (matched) {
                continue
            }

            if (pending.isNotEmpty()) {
                val positional = pending.first()
                val result = positional.accept(current, context)
                val finished = !positional.canAcceptMore()
                if (finished) {
                    pending.removeFirst()
                }
                if (result.count > 0 || result.failure != null || finished) {
                    if (result.failure != null) {
                        failure = result.failure
                    }
                    index += result.count
                    continue
                }
            }

            // Did not match anything
            break
        }

        if (failure == null) {
            for (positional in pending) {
                val missing = positional.finished(context)
                if (missing != null && failure == null) {
                    failure = missing
                }
            }
        }

        return ParseResult(index, failure)
    }

    private fun attemptToRecover(args: List<String>, original: ParseResult, host: Host, context: ParseContext): Result {
        var index = original.count
        while (index in args.indices) {
            val current = args.subList(index, args.size)
            for (option in options) {
                val result = option.maybeRecover(current, context)
                if (result) {
                    // Don't attempt to keep parsing
                    return Result.Success
                }
            }
            // Skip this argument and attempt to recover on next argument
            index++
        }

        val arg = args.getOrNull(original.count)
        val failure = when {
            arg != null && host.isOption(arg) -> {
                var matched = false
                for (option in options) {
                    val result = option.accepts(arg)
                    if (result) {
                        matched = true
                        break
                    }
                }
                if (matched && original.failure != null) {
                    original.failure
                } else {
                    ArgParseException("Unknown option: $arg")
                }
            }

            original.failure != null -> original.failure
            else -> ArgParseException("Unknown parameter: $arg")
        }
        return Result.Failure(failure)
    }

    open fun usage(): ActionUsage {
        return ActionUsage(
            options.flatMap { it.usage() },
            positional.map { it.usage() }
        )
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