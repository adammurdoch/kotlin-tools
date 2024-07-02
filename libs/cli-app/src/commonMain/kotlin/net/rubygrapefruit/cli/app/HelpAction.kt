package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action

internal class HelpAction(
    private val name: String,
    private val action: Action
) : Action() {
    private val actionName by parameter("action").optional()

    internal val formatted: String
        get() {
            if (actionName != null) {
                return "usage for '$actionName'"
            }
            val usage = action.usage().effective()
            val builder = StringBuilder()
            builder.append("Usage: ")
            builder.append(name)
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