package net.rubygrapefruit.cli

internal interface NonPositional {
    fun usage(): List<NonPositionalUsage>

    /**
     * Attempt to parse the given arguments.
     *
     * @param args Is never empty.
     */
    fun accept(args: List<String>, context: ParseContext): ParseResult

    /**
     * Attempt to continue parsing following a parse failure.
     *
     * @param args May be empty.
     */
    fun maybeRecover(args: List<String>, context: ParseContext): Boolean {
        return false
    }
}