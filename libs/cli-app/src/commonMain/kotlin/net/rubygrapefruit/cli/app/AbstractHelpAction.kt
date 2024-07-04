package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionUsage

internal abstract class AbstractHelpAction : Action() {
    abstract val formatted: String

    override fun run() {
        for (line in formatted.lines()) {
            println(line)
        }
    }

    protected fun StringBuilder.appendUsage(name: String, action: Action) {
        appendUsage(name, action.usage())
    }

    protected fun StringBuilder.appendUsage(name: String, action: ActionUsage) {
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