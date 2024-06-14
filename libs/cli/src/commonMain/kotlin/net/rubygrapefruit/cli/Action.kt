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
        val option = DefaultNullableStringOption(name, DefaultHost, this)
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
        val flag = DefaultFlag(name, DefaultHost, default)
        options.add(flag)
        return flag
    }

    /**
     * Defines an argument with the given name.
     * The argument must appear at the current location on the command-line.
     * Uses the default value if not present, and fails if the argument is not present and its default is null.
     */
    fun argument(name: String, default: String? = null, help: String? = null): Argument<String> {
        val arg = DefaultArgument(name, DefaultHost, default)
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
        val count = maybeParse(args)
        if (count < args.size) {
            val arg = args[count]
            if (DefaultHost.isOption(arg)) {
                throw ArgParseException("Unknown option: $arg")
            } else {
                throw ArgParseException("Unknown argument: $arg")
            }
        }
    }

    internal fun maybeParse(args: List<String>): Int {
        val pendingArgs = this.positional.toMutableList()

        var index = 0
        while (index in args.indices) {
            val current = args.subList(index, args.size)

            var matched = false
            for (option in options) {
                val count = option.accept(current)
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
                val count = pendingArgs.first().accept(current)
                pendingArgs.removeFirst()
                index += count
                continue
            }

            return index
        }

        for (arg in pendingArgs) {
            arg.missing()
        }
        return args.size
    }

    /**
     * Runs this action, using the given arguments to configure it.
     */
    fun run(args: Array<String>) {
        run(args.toList())
    }

    /**
     * Runs this action, using the given arguments to configure it.
     */
    fun run(args: List<String>) {
        try {
            parse(args)
        } catch (e: ArgParseException) {
            println(e.formattedMessage)
            exit(1)
        }

        run()
        exit(0)
    }

    internal fun replace(option: NonPositional, newOption: NonPositional) {
        options[options.indexOf(option)] = newOption
    }

    interface Actions {
        fun action(name: String, action: Action, help: String? = null)
    }
}