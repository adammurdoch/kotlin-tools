package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage
import net.rubygrapefruit.cli.ActionUsage
import net.rubygrapefruit.cli.ParameterUsage

internal fun Formatter.appendUsage(name: String, action: Action) {
    appendUsage(name, action.usage())
}

internal fun Formatter.appendUsage(name: String, action: ActionUsage) {
    val usage = action.effective()
    appendUsageSummary(name, usage)

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

internal fun Formatter.appendUsageSummary(name: String, usage: ActionUsage) {
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
}
