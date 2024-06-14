package net.rubygrapefruit.cli

class ArgParseException internal constructor(
    message: String,
    private val resolution: String? = null,
    internal val actions: List<SubActionUsage> = emptyList()
) : RuntimeException(message) {
    val formattedMessage: String
        get() {
            val builder = StringBuilder()
            builder.append(resolution ?: message)
            if (actions.isNotEmpty()) {
                builder.append("\n\nAvailable actions:\n")
                builder.appendItems(actions)
                builder.append("\n")
            }
            return builder.toString()
        }
}

internal class SubActionUsage(val name: String, help: String?) : ItemUsage(help) {
    override val display: String
        get() = name
}
