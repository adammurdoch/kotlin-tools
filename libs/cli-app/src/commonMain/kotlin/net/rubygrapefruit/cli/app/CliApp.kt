package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionUsage
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
                formatter.run {
                    append(e.resolution ?: e.message)
                    maybeNewLine()
                    if (e.actions.isNotEmpty()) {
                        newLine()
                        appendUsageSummary(name, main.usage().effective().dropOptions())
                        table("Available actions", e.actions) { Pair(it.name, it.help) }
                    }
                }
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
}