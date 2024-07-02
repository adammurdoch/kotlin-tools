package net.rubygrapefruit.cli

internal abstract class NonPositional {
    abstract fun usage(): List<OptionUsage>

    /**
     * Attempt to parse the given arguments, returning number of arguments consumed.
     */
    abstract fun accept(args: List<String>, context: ParseContext): ParseResult

    /**
     * Attempt to continue parsing following a parse failure.
     */
    open fun maybeRecover(args: List<String>, context: ParseContext): ParseResult {
        return ParseResult.Nothing
    }
}