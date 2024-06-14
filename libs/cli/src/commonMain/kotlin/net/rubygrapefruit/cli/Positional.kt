package net.rubygrapefruit.cli

internal sealed class Positional {
    abstract fun usage(): PositionalUsage
    abstract fun accept(args: List<String>): ParseResult
    abstract fun missing(): ArgParseException?
}