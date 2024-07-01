package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ArgParseException

/**
 * An [Action] that can be used as the main action for a CLI application.
 */
open class CliApp(val name: String) : CliAction() {

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
        val main = MainAction(this)
        try {
            val action = try {
                main.actionFor(args)
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
            if (!parsed || main.stackTrace) {
                t.printStackTrace()
            } else {
                println(t.message)
            }
            exit(1)
        }
    }

    internal fun actionFor(args: List<String>): Action {
        return MainAction(this).actionFor(args)
    }

    private class MainAction(val app: CliApp) : Action() {
        val stackTrace by flag("stack", help = "Show stack trace on failure")
        val action by actions {
            option(HelpAction(app.name, this@MainAction), "help", help = "Show usage message", allowAnywhere = true)
            option(CompletionAction(app.name, this@MainAction), "completion", help = "Generate ZSH completion script")
            action(app)
        }

        fun actionFor(args: List<String>): Action {
            val result = maybeParse(args)
            return if (action is HelpAction) {
                action
            } else if (result is Result.Failure) {
                throw result.failure
            } else {
                action
            }
        }
    }
}