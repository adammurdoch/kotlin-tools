package net.rubygrapefruit.cli

/**
 * An action that can be configured using command-line arguments.
 */
open class Action {
    private val options = mutableListOf<DefaultFlag>()
    private val positional = mutableListOf<PositionalArgument>()

    /**
     * Defines a boolean flag with the given name.
     */
    fun flag(name: String, default: Boolean = false): Flag {
        val flag = DefaultFlag(name, default)
        options.add(flag)
        return flag
    }

    /**
     * Defines an argument with the given name.
     */
    fun argument(name: String, default: String? = null): Argument<String> {
        val arg = DefaultArgument(name, default)
        positional.add(arg)
        return arg
    }

    /**
     * Defines a set of sub-actions.
     */
    fun actions(builder: Actions.() -> Unit): Argument<Action> {
        val actions = DefaultActionSet()
        builder(actions)
        positional.add(actions)
        return actions
    }

    protected open fun run() {}

    /**
     * Configures this object from the given arguments.
     */
    @Throws(ArgParseException::class)
    fun parse(args: List<String>) {
        val pendingArgs = this.positional.toMutableList()

        for (arg in args) {
            var matched = false
            for (option in options) {
                if (option.accept(arg)) {
                    matched = true
                    break
                }
            }
            if (matched) {
                continue
            }
            if (pendingArgs.isNotEmpty()) {
                pendingArgs.removeFirst().accept(arg)
            } else if (arg.startsWith("--")) {
                throw ArgParseException("Unknown option: $arg")
            } else {
                throw ArgParseException("Unknown argument: $arg")
            }
        }

        for (arg in pendingArgs) {
            arg.missing()
        }
    }

    fun run(args: List<String>): Nothing {
        parse(args)
        run()
        exit(0)
    }

    interface Actions {
        fun action(name: String, action: Action)
    }
}