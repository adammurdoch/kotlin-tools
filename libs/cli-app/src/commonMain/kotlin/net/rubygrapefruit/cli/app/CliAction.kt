package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ArgParseException

/**
 * An [Action] that can be used as the main action for a CLI application.
 */
open class CliAction(val name: String) : Action() {
    private val help by boolean().flag("help", help = "Show usage message", disableOption = false)
    private val completion by boolean().flag("completion", help = "Generate ZSH completion script", disableOption = false)
    private val stackTrace by flag("stack", help = "Show stack trace on failure")

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
        var parsed = false
        try {
            val action = try {
                actionFor(args)
            } catch (e: ArgParseException) {
                for (line in e.formattedMessage.lines()) {
                    println(line)
                }
                exit(1)
            }
            parsed = true

            action.run()
            exit(0)
        } catch (t: Throwable) {
            if (!parsed || stackTrace) {
                t.printStackTrace()
            } else {
                println(t.message)
            }
            exit(1)
        }
    }

    internal fun actionFor(args: List<String>): Action {
        val result = maybeParse(args)
        return if (help) {
            HelpAction(this)
        } else if (completion) {
            CompletionAction(this)
        } else if (result is Failure) {
            throw result.failure
        } else {
            this
        }
    }
}