package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionUsage

internal abstract class AbstractHelpAction : Action() {
    protected fun Formatter.appendUsage(name: String, action: Action) {
        appendUsage(name, action.usage())
    }

    protected fun Formatter.appendUsage(name: String, action: ActionUsage) {
        val usage = action.effective()
        append("Usage: ")
        append(name)
        val usageFormatted = usage.formatted
        if (usageFormatted.isNotEmpty() && usageFormatted[0] != '\n') {
            append(" ")
        }
        append(usageFormatted)
    }
}