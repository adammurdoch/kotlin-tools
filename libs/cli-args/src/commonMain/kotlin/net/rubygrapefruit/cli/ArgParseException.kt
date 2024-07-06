package net.rubygrapefruit.cli

class ArgParseException internal constructor(
    message: String,
    val resolution: String? = null,
    val actions: List<NamedNestedActionUsage> = emptyList(),
    cause: Throwable? = null
) : RuntimeException(message, cause)
