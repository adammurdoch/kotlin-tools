package net.rubygrapefruit.cli

open class MainAction(private val name: String) : Action() {
    private val help by flag("help", help = "Show usage message")

    override fun usage(): ActionUsage {
        val usage = super.usage()
        return ActionUsage(name, usage.options, usage.positional)
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
        val action = try {
            actionFor(args)
        } catch (e: ArgParseException) {
            for (line in e.formattedMessage.lines()) {
                println(line)
            }
            exit(1)
        }

        action.run()
        exit(0)
    }

    internal fun actionFor(args: List<String>): Action {
        val result = parseAll(args)
        return if (help) {
            HelpAction(this)
        } else if (result.failure != null) {
            throw result.failure
        } else {
            this
        }
    }
}