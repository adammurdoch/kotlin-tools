package net.rubygrapefruit.cli

class ArgParseException internal constructor(
    message: String,
    private val resolution: String? = null,
    internal val actions: List<SubActionInfo> = emptyList()
) : RuntimeException(message) {
    val formattedMessage: String
        get() {
            val builder = StringBuilder()
            builder.append(resolution ?: message)
            if (actions.isNotEmpty()) {
                val nameWidth = actions.maxOf { it.name.length }
                builder.append("\n\nAvailable actions:\n")
                for (index in actions.indices) {
                    val action = actions[index]
                    if (index > 0) {
                        builder.append("\n")
                    }
                    if (action.help != null) {
                        val padded = action.name.padEnd(nameWidth)
                        builder.append("  $padded ${action.help}")
                    } else {
                        builder.append("  ${action.name}")
                    }
                }
                builder.append("\n")
            }
            return builder.toString()
        }
}

internal class SubActionInfo(val name: String, val help: String?)
