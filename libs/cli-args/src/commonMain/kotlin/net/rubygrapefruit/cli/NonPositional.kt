package net.rubygrapefruit.cli

internal interface NonPositional {
    fun usage(): List<OptionUsage>

    /**
     * Does this non-positional accept the given arg as its first argument?
     */
    fun accepts(arg: String): Boolean

    /**
     * Attempt to parse the given arguments, returning number of arguments consumed.
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