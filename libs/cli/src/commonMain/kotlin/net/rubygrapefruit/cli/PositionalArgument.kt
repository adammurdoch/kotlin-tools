package net.rubygrapefruit.cli

internal sealed class PositionalArgument {
    abstract fun accept(args: List<String>): Int
    abstract fun missing()
}