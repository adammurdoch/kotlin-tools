package net.rubygrapefruit.cli

internal sealed class Positional {
    abstract fun usage(): PositionalUsage

    /**
     * Attempt to parse the given arguments, returning number of arguments consumed.
     *
     * @param args Is never empty.
     */
    abstract fun accept(args: List<String>, context: ParseContext): ParseResult

    /**
     * Called after [accept]. When the return value is `false`, this positional will not be called any further.
     */
    abstract fun canAcceptMore(): Boolean

    /**
     * Called when parsing is finished and [accept] will not be called again. Allows a specific exception to be generated.
     */
    abstract fun finished(context: ParseContext): ArgParseException?
}