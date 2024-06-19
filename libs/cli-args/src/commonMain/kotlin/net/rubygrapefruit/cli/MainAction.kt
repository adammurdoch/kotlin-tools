package net.rubygrapefruit.cli

/**
 * An [Action] that can be used as the main action for a CLI application.
 */
open class MainAction(private val name: String) : Action() {
    private val help by simpleFlag("help", help = "Show usage message")
    private val completion by simpleFlag("completion", help = "Generate ZSH completion script")
    private val stackTrace by flag("stack", help = "Show stack trace on failure")

    override fun usage(): ActionUsage {
        val usage = super.usage()
        return ActionUsage(name, usage.options, usage.positional)
    }

    /**
     * Runs this action, using the given arguments to configure it. Does not return
     */
    fun run(args: Array<String>) {
        run(args.toList())
    }

    /**
     * Runs this action, using the given arguments to configure it. Does not return
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

        try {
            action.run()
            exit(0)
        } catch (t: Throwable) {
            if (stackTrace) {
                t.printStackTrace()
            } else {
                println(t.message)
            }
            exit(1)
        }
    }

    internal fun actionFor(args: List<String>): Action {
        val result = parseAll(args)
        return if (help) {
            HelpAction(this)
        } else if (completion) {
            CompletionAction(this)
        } else if (result.failure != null) {
            throw result.failure
        } else {
            this
        }
    }
}