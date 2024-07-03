package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ActionParameterUsage

internal class HelpAction(
    private val name: String,
    private val action: Action
) : Action() {
    private val actionName by parameter("action").optional()

    internal val formatted: String
        get() {
            val builder = StringBuilder()
            if (actionName != null) {
                val positional = action.usage().effective().positional.firstOrNull()
                val nestedAction = if (positional is ActionParameterUsage) {
                    positional.named.find { it.name == actionName }
                } else {
                    null
                }
                if (nestedAction != null) {
                    val usage = nestedAction.action.effective()
                    builder.append("Usage: ")
                    builder.append(actionName)
                    builder.append(" ")
                    builder.append(usage.formatted)
                } else {
                    builder.append("Unknown action: $actionName")
                }
            } else {
                val usage = action.usage().effective()
                builder.append("Usage: ")
                builder.append(name)
                val usageFormatted = usage.formatted
                if (usageFormatted.isNotEmpty() && usageFormatted[0] != '\n') {
                    builder.append(" ")
                }
                builder.append(usageFormatted)
            }
            return builder.toString()
        }

    override fun run() {
        for (line in formatted.lines()) {
            println(line)
        }
    }
}