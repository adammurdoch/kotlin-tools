package net.rubygrapefruit.cli

internal sealed class Positional {
    abstract fun usage(): PositionalUsage

    /**
     * Attempt to parse the given arguments, returning number of arguments consumed. If the result is 'finished', no further methods will be called on this positional.
     */
    abstract fun accept(args: List<String>, context: ParseContext): ParseResult

    /**
     * Called when parsing is finished and [accept] will not be called again. Allows a specific exception to be generated.
     */
    abstract fun finished(): ArgParseException?
}