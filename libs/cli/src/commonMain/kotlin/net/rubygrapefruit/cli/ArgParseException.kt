package net.rubygrapefruit.cli

class ArgParseException internal constructor(
    message: String,
    private val resolution: String? = null,
    private val actions: List<SubActionInfo> = emptyList()
) : RuntimeException(message) {
    val formattedMessage: String
        get() {
            val builder = StringBuilder()
            builder.append(resolution ?: message)
            if (actions.isNotEmpty()) {
                builder.append("\n\nAvailable actions:\n")
                for (action in actions) {
                    builder.append("  ${action.name} ${action.help}\n")
                }
            }
            return builder.toString()
        }
}

internal class SubActionInfo(val name: String, val help: String?)
