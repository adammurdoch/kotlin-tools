package net.rubygrapefruit.cli

internal interface Positional: HasPositionalUsage {
    fun usage(name: String): ActionUsage?

    /**
     * Attempt to parse the given arguments, returning number of arguments consumed.
     *
     * @param args Is never empty.
     */
    fun accept(args: List<String>, context: ParseContext): ParseResult

    /**
     * Called after [accept]. When the return value is `false`, this positional will not be called any further.
     */
    fun canAcceptMore(): Boolean

    /**
     * Called when parsing is finished and [accept] will not be called again. Allows a specific exception to be generated when this positional is missing.
     */
    fun finished(context: ParseContext): FinishResult
}