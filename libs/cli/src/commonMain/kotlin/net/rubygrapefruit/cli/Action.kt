package net.rubygrapefruit.cli

/**
 * An action that can be configured using command-line arguments.
 */
open class Action {
    private val options = mutableListOf<NonPositional>()
    private val positional = mutableListOf<Positional>()

    internal fun simpleFlag(name: String, help: String? = null): Flag {
        val flag = DefaultFlag(listOf(name), false, help, DefaultHost, false, this)
        options.add(flag)
        return flag
    }

    /**
     * Defines a string option with the given names. Can use `--<name> <value>` to specify the value.
     * For single character names, use `-<name> <value>` to specify the value.
     *
     * The option can appear anywhere in the command-line. It can only appear once.
     * Has value `null` when the option is not present in the input. Use [NullableOption.whenAbsent] to use a different default.
     *
     * Use [NullableStringOption.int] to convert the value to an `int`
     */
    fun option(name: String, vararg names: String, help: String? = null): NullableStringOption {
        val allNames = listOf(name) + names.toList()
        allNames.forEach { DefaultHost.validate(it, "an option name") }

        val option = DefaultNullableStringOption(allNames, help, DefaultHost, this)
        options.add(option)
        return option
    }

    /**
     * Defines a boolean flag with the given names. Can use `--<name>` or `--no-<name>` to specify the value.
     * For single character names, use `-<name>` to specify the value.
     *
     * The flag can appear anywhere in the command-line. It can be specified multiple times and the last value is used.
     * Has value `false` when the flag is not present in the input. Use [Flag.whenAbsent] to use a different default.
     */
    fun flag(name: String, vararg names: String, help: String? = null): Flag {
        val allNames = listOf(name) + names.toList()
        allNames.forEach { DefaultHost.validate(it, "a flag name") }

        val flag = DefaultFlag(allNames, true, help, DefaultHost, false, this)
        options.add(flag)
        return flag
    }

    /**
     * Defines a set of values that can be selected using flags.
     *
     * The flags can appear anywhere in the input. They can be specified multiple times and the last value is used.
     * Has value `null` then none of the flags is present in the input. Use [NullableOption.whenAbsent] to use a different default.
     */
    fun <T : Any> oneOf(builder: Choices<T>.() -> Unit): NullableOption<T> {
        val choices = DefaultChoices<T>(DefaultHost)
        builder(choices)
        val option = DefaultNullableChoice<T>(choices.choices, this)
        options.add(option)
        return option
    }

    /**
     * Defines a parameter with the given name.
     *
     * The parameter must appear at a specific location in the input.
     * Fails if the parameter is not present. Use [Parameter.whenAbsent] to allow the parameter to be missing.
     */
    fun parameter(name: String, help: String? = null): StringParameter<String> {
        val arg = DefaultStringParameter(name, help, null, DefaultHost, this)
        positional.add(arg)
        return arg
    }

    /**
     * Defines a multi-value parameter with the given name.
     *
     * The parameter must appear at a specific location in the input.
     * Uses an empty list if the parameter is not present in the input. Use [Parameter.whenAbsent] to use a different default.
     */
    fun parameters(name: String, help: String? = null): ListParameter<String> {
        val arg = MultiValueParameter(name, help, DefaultHost, this, emptyList(), false)
        positional.add(arg)
        return arg
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
        val parameter = DefaultActionSet<T>(actions.actions, DefaultHost, this, null)
        positional.add(parameter)
        return parameter
    }

    open fun run() {}

    /**
     * Configures this object from the given arguments.
     */
    @Throws(ArgParseException::class)
    fun parse(args: List<String>) {
        val result = parseAll(args)
        if (result.failure != null) {
            throw result.failure
        }
    }

    internal fun parseAll(args: List<String>): ParseResult {
        val result = maybeParse(args, RootContext)
        val count = result.count
        if (result.failure == null && count < args.size) {
            val arg = args[count]
            val failure = if (DefaultHost.isOption(arg)) {
                ArgParseException("Unknown option: $arg")
            } else {
                ArgParseException("Unknown parameter: $arg")
            }
            return ParseResult(count, failure, true)
        }
        return result
    }

    internal fun maybeParse(args: List<String>, parent: ParseContext): ParseResult {
        val pending = this.positional.toMutableList()
        val context = parent.withOptions(options)

        var index = 0
        var failure: ArgParseException? = null
        while (index in args.indices) {
            val current = args.subList(index, args.size)

            var matched = false
            for (option in context.options) {
                val result = option.accept(current)
                if (result.failure != null && failure == null) {
                    failure = result.failure
                }
                val count = result.count
                if (count > 0) {
                    index += count
                    matched = true
                    break
                }
            }
            if (matched) {
                continue
            }

            if (pending.isNotEmpty()) {
                val result = pending.first().accept(current, context)
                if (result.failure != null) {
                    return result.advance(index)
                }
                if (result.finished) {
                    pending.removeFirst()
                }
                if (result.count > 0) {
                    index += result.count
                    continue
                }
                // Could not match anything
            }

            return ParseResult(index, failure, true)
        }

        if (failure == null) {
            for (positional in pending) {
                val missing = positional.missing()
                if (missing != null && failure == null) {
                    failure = missing
                }
            }
        }

        return ParseResult(args.size, failure, true)
    }

    internal open fun usage(): ActionUsage {
        return ActionUsage(
            null,
            options.flatMap { it.usage() },
            positional.map { it.usage() }
        )
    }

    internal fun <T : NonPositional> replace(option: NonPositional, newOption: T): T {
        options[options.indexOf(option)] = newOption
        return newOption
    }

    internal fun <T : Positional> replace(param: Positional, newParam: T): T {
        positional[positional.indexOf(param)] = newParam
        return newParam
    }

    interface Actions<T : Action> {
        fun action(action: T, name: String, help: String? = null)
    }

    interface Choices<T> {
        fun choice(value: T, name: String, vararg names: String, help: String? = null)
    }
}