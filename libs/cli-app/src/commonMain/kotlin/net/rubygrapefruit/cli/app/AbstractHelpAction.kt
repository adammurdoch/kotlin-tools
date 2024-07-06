package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage
import net.rubygrapefruit.cli.ActionUsage
import net.rubygrapefruit.cli.ParameterUsage

internal abstract class AbstractHelpAction : Action() {
    protected fun Formatter.appendUsage(name: String, action: Action) {
        appendUsage(name, action.usage())
    }

    protected fun Formatter.appendUsage(name: String, action: ActionUsage) {
        val usage = action.effective()
        append("Usage: ")
        append(name)

        if (usage.options.isNotEmpty()) {
            append(" [options]")
        }
        for (positional in usage.positional) {
            append(" ")
            append(positional.usage)
        }
        newLine()

        val parameters = usage.positional.filterIsInstance<ParameterUsage>().filter { it.help != null }
        val first = usage.positional.firstOrNull()
        val actions = if (first is ActionParameterUsage) {
            first.actions
        } else {
            emptyList()
        }
        table("Parameters", parameters) { Pair(it.display, it.help) }
        table("Actions", actions.sortedBy { it.display }) { Pair(it.display, it.help) }
        table("Options", usage.options.sortedBy { it.display }) { Pair(it.display, it.help) }
    }
}