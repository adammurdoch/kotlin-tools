package net.rubygrapefruit.cli

class PositionalParseException internal constructor(
    message: String,
    val resolution: String = message,
    val positional: List<PositionalUsage> = emptyList(),
    val actions: List<NamedNestedActionUsage> = emptyList()
) : ArgParseException(message, null)