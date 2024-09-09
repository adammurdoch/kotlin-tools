package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionUsage
import net.rubygrapefruit.cli.ArgParseException
import net.rubygrapefruit.cli.PositionalParseException

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
                reportParseFailure(formatter, e)
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

    private fun reportParseFailure(formatter: Formatter, e: ArgParseException) {
        formatter.run {
            if (e is PositionalParseException) {
                append(e.resolution)
                maybeNewLine()
                newLine()
                val usage = ActionUsage(emptyList(), e.positional)
                appendUsageSummary(name, usage)
                appendParameters(usage)
                table("Available actions", e.actions) { Pair(it.name, it.help) }
            } else {
                append(e.message)
                maybeNewLine()
            }
        }
    }

    internal fun actionFor(args: List<String>, formatter: Formatter): Action {
        return MainAction(this, formatter).actionFor(args)
    }
}