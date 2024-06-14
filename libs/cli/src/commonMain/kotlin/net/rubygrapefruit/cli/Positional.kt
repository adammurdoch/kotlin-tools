package net.rubygrapefruit.cli

internal sealed class Positional {
    abstract fun accept(args: List<String>): Int
    abstract fun missing()
}