package net.rubygrapefruit.cli

class PositionalParseException internal constructor(
    message: String,
    val resolution: String = message,
    val actions: List<NamedNestedActionUsage> = emptyList()
) : ArgParseException(message, null)