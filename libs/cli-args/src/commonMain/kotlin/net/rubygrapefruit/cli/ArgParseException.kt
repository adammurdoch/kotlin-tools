package net.rubygrapefruit.cli

class ArgParseException internal constructor(
    message: String,
    private val resolution: String? = null,
    internal val actions: List<NamedNestedActionUsage> = emptyList(),
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    val formattedMessage: String
        get() {
            val builder = StringBuilder()
            builder.append(resolution ?: message)
            builder.appendItems("Available actions", actions, false)
            return builder.toString()
        }
}
