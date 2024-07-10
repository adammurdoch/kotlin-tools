package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.*

internal fun Formatter.appendUsage(name: String, action: Action) {
    appendUsage(name, action.usage())
}

internal fun Formatter.appendUsage(name: String, action: ActionUsage) {
    val usage = action.effective()
    appendUsageSummary(name, usage)

    val first = usage.positional.firstOrNull()
    val actions = if (first is ActionParameterUsage) {
        first.actions
    } else {
        emptyList()
    }
    appendParameters(usage)
    table("Actions", actions.sortedBy { it.display }) { Pair(it.display, it.help) }
    table("Options", usage.options.sortedBy { it.usage }) { Pair(it.usage, it.help) }
}

internal fun Formatter.appendParameters(usage: ActionUsage) {
    val parameters = usage.positional.filterIsInstance<ParameterUsage>().filter { it.help != null }
    table("Parameters", parameters) { Pair(it.display, it.help) }
}

internal fun Formatter.appendUsageSummary(name: String, action: ActionUsage) {
    appendUsageSummary(PrefixedActionUsage(listOf(LiteralUsage(name, null)), action))
}

internal fun Formatter.appendUsageSummary(usage: PrefixedActionUsage) {
    append("Usage:")
    for (positional in usage.prefix) {
        append(" ")
        append(positional.usage)
    }

    if (usage.action.options.isNotEmpty()) {
        append(" [options]")
    }
    for (positional in usage.action.positional) {
        append(" ")
        append(positional.usage)
    }
    newLine()
}
