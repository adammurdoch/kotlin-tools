package net.rubygrapefruit.cli

/**
 * A positional parameter.
 */
internal interface Positional : HasPositionalUsage {
    fun usage(name: String): ActionUsage?

    fun start(context: ParseContext): ParseState
}