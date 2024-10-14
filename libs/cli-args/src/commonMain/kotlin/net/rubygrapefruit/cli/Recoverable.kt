package net.rubygrapefruit.cli

internal interface Recoverable {
    /**
     * Attempt to continue parsing following a parse failure.
     *
     * @param args May be empty.
     */
    fun maybeRecover(args: List<String>, context: ParseContext): Boolean
}