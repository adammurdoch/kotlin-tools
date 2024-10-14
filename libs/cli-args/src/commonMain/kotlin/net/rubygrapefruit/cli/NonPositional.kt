package net.rubygrapefruit.cli

internal interface NonPositional {
    fun usage(): List<NonPositionalUsage>

    /**
     * Attempt to parse the given arguments.
     *
     * @param args Is never empty.
     */
    fun accept(args: List<String>, context: ParseContext): ParseResult

    fun accepts(option: String): Boolean
}