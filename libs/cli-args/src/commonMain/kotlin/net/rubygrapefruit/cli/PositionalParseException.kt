package net.rubygrapefruit.cli

/**
 * Thrown on a failure to parse a positional argument.
 */
class PositionalParseException internal constructor(
    message: String,
    val resolution: String = message,
    val positional: List<PositionalUsage> = emptyList(),
    val actions: List<NamedNestedActionUsage> = emptyList()
) : ArgParseException(message, null)