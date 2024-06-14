package net.rubygrapefruit.cli

internal sealed class PositionalArgument {
    abstract fun accept(arg: String)
    abstract fun missing()
}