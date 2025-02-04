package net.rubygrapefruit.cli

internal interface Positional : HasPositionalUsage {
    fun usage(name: String): ActionUsage?

    fun start(context: ParseContext): ParseState
}