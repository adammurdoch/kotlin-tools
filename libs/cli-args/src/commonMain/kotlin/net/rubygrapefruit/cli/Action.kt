package net.rubygrapefruit.cli

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
    fun <T : Any> oneOf(builder: Choices<T>.() -> Unit): MappingConfigurationBuilder<T> {
        val choices = DefaultChoices<T>(DefaultHost)
        builder(choices)
        return DefaultMappingConfigurationBuilder(this, DefaultHost, ChoiceConverter(choices.choices))
    }

    /**
     * Allows configuration values of type [T] to be added to this action.
     * Uses the provided function to convert from string values to [T]
     */
    fun <T : Any> type(converter: (String) -> T?): ConfigurationBuilder<T> {
        return DefaultConfigurationBuilder(this, DefaultHost, MappingConverter(converter))
    }

    /**
     * Allows configuration values of type [FilePath] to be added to this action.
     */
    fun path(): ConfigurationBuilder<FilePath> {
        return type { FilePath(it) }
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
    fun parameter(name: String, help: String? = null): Parameter<String> {
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
     * Fails if an action is not present in the input. Use [Parameter.whenAbsent] to use a different default.
     */
    fun <T : Action> actions(builder: Actions<T>.() -> Unit): Parameter<T> {
        val actions = DefaultActions<T>(DefaultHost)
        builder(actions)
        val parameter = DefaultActionParameter(actions.actions, DefaultHost, this, null)
        positional.add(parameter)
        return parameter
    }

    open fun run() {}

    /**
     * Configures this object from the given arguments, throwing an [ArgParseException] when the arguments could not be parsed.
     */
    @Throws(ArgParseException::class)
    fun parse(args: List<String>) {
        val result = maybeParse(args)
        if (result is Failure) {
            throw result.failure
        }
    }

    /**
     * Configures this object from the given arguments.
     */
    fun maybeParse(args: List<String>): Result {
        val result = maybeParse(args, RootContext, stopOnFailure = false)
        return if (result.failure == null) {
            Success
        } else {
            Failure(result.failure)
        }
    }

    internal fun maybeParse(args: List<String>, parent: ParseContext, stopOnFailure: Boolean): ParseResult {
        val pending = this.positional.toMutableList()
        val context = parent.withOptions(options)

        var index = 0
        var failure: ArgParseException? = null
        while (index in args.indices && (!stopOnFailure || failure == null)) {
            val current = args.subList(index, args.size)

            var matched = false
            for (option in context.options) {
                val result = option.accept(current)
                if (result.count > 0) {
                    if (result.failure != null && failure == null) {
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
                val result = pending.first().accept(current, context)
                if (result.count > 0) {
                    if (result.failure != null && failure == null) {
                        failure = result.failure
                    }
                    if (result.finished) {
                        pending.removeFirst()
                    }
                    index += result.count
                    continue
                }
            }

            // Did not match anything

            if (stopOnFailure) {
                // This is fine
                break
            }
            if (failure == null) {
                val arg = current.first()
                failure = if (DefaultHost.isOption(arg)) {
                    ArgParseException("Unknown option: $arg")
                } else {
                    ArgParseException("Unknown parameter: $arg")
                }
            }
            index++
        }

        if (failure == null) {
            for (positional in pending) {
                val missing = positional.missing()
                if (missing != null && failure == null) {
                    failure = missing
                }
            }
        }

        return ParseResult(index, failure, true)
    }

    open fun usage(): ActionUsage {
        return ActionUsage(
            null,
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

    sealed class Result
    data object Success : Result()
    class Failure(val failure: ArgParseException) : Result()

    interface Actions<T : Action> {
        fun action(action: T, name: String, help: String? = null)
    }

    interface Choices<T> {
        fun choice(value: T, name: String, vararg names: String, help: String? = null)
    }
}