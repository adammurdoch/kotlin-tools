package net.rubygrapefruit.cli

/**
 * A named parameter.
 */
internal interface Named {
    fun usage(): List<NonPositionalUsage>

    fun start(context: ParseContext): ParseState

    fun accepts(option: String): Boolean
}