package net.rubygrapefruit.cli

internal interface Recoverable {
    /**
     * Attempt to continue parsing following a parse failure.
     */
    fun maybeRecover(context: ParseContext): ParseState
}