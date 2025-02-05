package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage

internal class MainAction(val app: CliApp, formatter: Formatter) : Action() {
    val stackTrace by flag("stack", help = "Show stack trace on failure")
    val action by action {
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
        return if (result is Result.Failure) {
            throw result.failure
        } else {
            action
        }
    }
}