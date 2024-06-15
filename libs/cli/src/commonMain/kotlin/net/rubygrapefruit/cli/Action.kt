package net.rubygrapefruit.cli

/**
 * An action that can be configured using command-line arguments.
 */
open class Action {
    private val options = mutableListOf<NonPositional>()
    private val positional = mutableListOf<Positional>()

    internal fun simpleFlag(name: String, help: String? = null): Flag {
        val flag = DefaultFlag(listOf(name), false, help, DefaultHost, false)
        options.add(flag)
        return flag
    }

    /**
     * Defines a string option with the given names. Can use `--<name> <value>` to specify the value.
     * For single character names, use `-<name> <value>` to specify the value.
     *
     * The option can appear anywhere in the command-line. It can only appear once.
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
     * The flag can appear anywhere in the command-line. It can be specified multiple times and last value is used.
     * Uses the default value if not present.
     */
    fun flag(name: String, vararg names: String, default: Boolean = false, help: String? = null): Flag {
        val allNames = listOf(name) + names.toList()
        allNames.forEach { DefaultHost.validate(it, "a flag name") }

        val flag = DefaultFlag(allNames, true, help, DefaultHost, default)
        options.add(flag)
        return flag
    }

    /**
     * Defines a set of values that can be selected using flags.
     */
    fun <T> oneOf(builder: Choices<T>.() -> Unit): Option<T?> {
        val choice = DefaultChoice<T>(DefaultHost)
        builder(choice)
        options.add(choice)
        return choice
    }

    /**
     * Defines a parameter with the given name.
     *
     * The parameter must appear at the current location on the command-line.
     * Uses the default value if the parameter is not present in the input, or fails if the parameter is not present and its default is null.
     */
    fun parameter(name: String, default: String? = null, help: String? = null): Parameter<String> {
        val arg = DefaultParameter(name, help, DefaultHost, default)
        positional.add(arg)
        return arg
    }

    /**
     * Defines a multi-value parameter with the given name.
     *
     * The parameter must appear at the current location on the command-line.
     * Uses an empty list if the parameter is not present in the input.
     */
    fun parameters(name: String, help: String? = null): Parameter<List<String>> {
        val arg = MultiValueParameter(name, help, DefaultHost)
        positional.add(arg)
        return arg
    }

    /**
     * Defines a set of actions. Can use `<name> <args>` to invoke the action.
     *
     * Only one action can be invoked, and this must appear at the current location on the command-line.
     * Fails if an action is not present in the input.
     */
    fun actions(builder: Actions.() -> Unit): Parameter<Action> {
        val actions = DefaultActionSet(DefaultHost)
        builder(actions)
        positional.add(actions)
        return actions
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
        val result = maybeParse(args)
        val count = result.count
        if (result.failure == null && count < args.size) {
            val arg = args[count]
            val failure = if (DefaultHost.isOption(arg)) {
                ArgParseException("Unknown option: $arg")
            } else {
                ArgParseException("Unknown parameter: $arg")
            }
            return ParseResult(count, failure)
        }
        return result
    }

    internal fun maybeParse(args: List<String>): ParseResult {
        val pending = this.positional.toMutableList()

        var index = 0
        while (index in args.indices) {
            val current = args.subList(index, args.size)

            var matched = false
            for (option in options) {
                val result = option.accept(current)
                if (result.failure != null) {
                    return result.advance(index)
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
                val result = pending.first().accept(current)
                if (result.failure != null) {
                    return result.advance(index)
                }
                pending.removeFirst()
                index += result.count
                continue
            }

            return ParseResult(index, null)
        }

        for (positional in pending) {
            val failure = positional.missing()
            if (failure != null) {
                return ParseResult(args.size, failure)
            }
        }

        return ParseResult(args.size, null)
    }

    internal open fun usage(): ActionUsage {
        return ActionUsage(
            null,
            options.flatMap { it.usage() },
            positional.map { it.usage() }
        )
    }

    internal fun replace(option: NonPositional, newOption: NonPositional) {
        options[options.indexOf(option)] = newOption
    }

    interface Actions {
        fun action(action: Action, name: String, help: String? = null)
    }

    interface Choices<T> {
        fun choice(value: T, name: String, vararg names: String, help: String? = null)
    }
}