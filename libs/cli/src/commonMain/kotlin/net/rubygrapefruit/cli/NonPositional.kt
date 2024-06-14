package net.rubygrapefruit.cli

internal sealed class NonPositional {
    abstract fun accept(args: List<String>): ParseResult
}