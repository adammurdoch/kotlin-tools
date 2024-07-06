package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage
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
        val result = run(args, LoggingFormatter)
        if (result) {
            exit(0)
        } else {
            exit(1)
        }
    }

    internal fun run(args: List<String>, formatter: Formatter): Boolean {
        var parsed = false
        val main = MainAction(this, formatter)
        try {
            val action = try {
                main.actionFor(args)
            } catch (e: ArgParseException) {
                formatter.append(e.formattedMessage)
                formatter.maybeNewLine()
                return false
            }
            parsed = true

            action.run()
            return true
        } catch (t: Throwable) {
            if (!parsed || main.stackTrace) {
                t.printStackTrace()
            } else {
                formatter.append(t.message)
                formatter.maybeNewLine()
            }
            return false
        }
    }

    internal fun actionFor(args: List<String>, formatter: Formatter): Action {
        return MainAction(this, formatter).actionFor(args)
    }

    private class MainAction(val app: CliApp, formatter: Formatter) : Action() {
        val stackTrace by flag("stack", help = "Show stack trace on failure")
        val action by actions {
            val positional = app.usage().effective().positional.firstOrNull()
            if (positional is ActionParameterUsage) {
                option(NestedActionHelpAction(app.name, this@MainAction, formatter), "help", help = "Show usage message", allowAnywhere = true)
                action(NestedActionHelpAction(app.name, this@MainAction, formatter), "help", help = "Show usage message")
            } else {
                option(HelpAction(app.name, this@MainAction, formatter), "help", help = "Show usage message", allowAnywhere = true)
            }

            option(CompletionAction(app.name, this@MainAction, formatter), "completion", help = "Generate ZSH completion script")
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