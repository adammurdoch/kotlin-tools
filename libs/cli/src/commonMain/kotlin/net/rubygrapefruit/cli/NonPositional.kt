package net.rubygrapefruit.cli

internal sealed class NonPositional {
    abstract fun usage(): List<OptionUsage>

    abstract fun accept(args: List<String>): ParseResult
}