package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action

internal class HelpAction(val action: CliApp) : Action() {
    internal val formatted: String
        get() {
            val usage = action.usage()
            val builder = StringBuilder()
            builder.append("Usage: ")
            builder.append(action.name)
            val usageFormatted = usage.formatted
            if (usageFormatted.isNotEmpty() && usageFormatted[0] != '\n') {
                builder.append(" ")
            }
            builder.append(usageFormatted)
            return builder.toString()
        }

    override fun run() {
        for (line in formatted.lines()) {
            println(line)
        }
    }
}