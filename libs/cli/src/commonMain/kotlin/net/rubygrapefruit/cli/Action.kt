package net.rubygrapefruit.cli

/**
 * An action that can be configured using command-line arguments.
 */
open class Action {
    private val options = mutableListOf<NonPositional>()
    private val positional = mutableListOf<Positional>()

    /**
     * Defines a string option with the given name. Can use `--<name> <value>` to specify the value.
     * The flag can appear anywhere in the command-line.
     */
    fun option(name: String, help: String? = null): NullableStringOption {
        DefaultHost.validate(name, "an option name")
        val option = DefaultNullableStringOption(name, help, DefaultHost, this)
        options.add(option)
        return option
    }

    /**
     * Defines a boolean flag with the given name. Can use `--<name>` or `--no-<name>` to specify the value.
     * Uses the default value if not present.
     * The flag can appear anywhere in the command-line. It can be specified multiple times and last value is used.
     */
    fun flag(name: String, default: Boolean = false, help: String? = null): Flag {
        DefaultHost.validate(name, "a flag name")
        val flag = DefaultFlag(name, help, DefaultHost, default)
        options.add(flag)
        return flag
    }

    /**
     * Defines an argument with the given name.
     * The argument must appear at the current location on the command-line.
     * Uses the default value if not present, and fails if the argument is not present and its default is null.
     */
    fun argument(name: String, default: String? = null, help: String? = null): Argument<String> {
        val arg = DefaultArgument(name, help, DefaultHost, default)
        positional.add(arg)
        return arg
    }

    /**
     * Defines a set of sub-actions. Can use `<name> <args>` to invoke the action.
     * Only one sub-action can be invoked, and this must appear at the current location on the command-line.
     * Fails if a sub-action is not present.
     */
    fun actions(builder: Actions.() -> Unit): Argument<Action> {
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
                ArgParseException("Unknown argument: $arg")
            }
            return ParseResult(count, failure)
        }
        return result
    }

    internal fun maybeParse(args: List<String>): ParseResult {
        val pendingArgs = this.positional.toMutableList()

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

            if (pendingArgs.isNotEmpty()) {
                val result = pendingArgs.first().accept(current)
                if (result.failure != null) {
                    return result.advance(index)
                }
                pendingArgs.removeFirst()
                index += result.count
                continue
            }

            return ParseResult(index, null)
        }

        for (arg in pendingArgs) {
            val failure = arg.missing()
            if (failure != null) {
                return ParseResult(args.size, failure)
            }
        }

        return ParseResult(args.size, null)
    }

    internal fun usage(): ActionUsage {
        return ActionUsage(
            options.flatMap { it.usage() },
            positional.map { it.usage() }
        )
    }

    internal fun replace(option: NonPositional, newOption: NonPositional) {
        options[options.indexOf(option)] = newOption
    }

    interface Actions {
        fun action(name: String, action: Action, help: String? = null)
    }
}