package net.rubygrapefruit.cli

/**
 * A named parameter.
 */
internal interface Named {
    /**
     * Returns the list of markers that this parameter can start with.
     */
    val markers: List<String>

    fun usage(): List<NonPositionalUsage>

    fun start(context: ParseContext): ParseState
}