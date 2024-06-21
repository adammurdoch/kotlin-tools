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
            builder.appendItems("Available actions", actions, false)
            return builder.toString()
        }
}