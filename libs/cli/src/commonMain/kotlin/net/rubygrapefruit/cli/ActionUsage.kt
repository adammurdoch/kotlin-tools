package net.rubygrapefruit.cli

internal class ActionUsage(
    val options: List<OptionUsage>,
    val positional: List<PositionalUsage>
)

internal class OptionUsage(
    val usage: String
)

internal class PositionalUsage(
    val usage: String
)
