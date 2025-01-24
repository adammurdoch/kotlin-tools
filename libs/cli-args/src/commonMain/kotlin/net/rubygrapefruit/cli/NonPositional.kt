package net.rubygrapefruit.cli

internal interface NonPositional {
    fun usage(): List<NonPositionalUsage>

    val inheritable: Boolean
        get() = true

    /**
     * Attempt to parse the given arguments.
     *
     * @param args Is never empty.
     */
    fun accept(args: List<String>, context: ParseContext): ParseResult

    fun accepts(option: String): Boolean

    /**
     * Called when parsing is finished and [accept] will not be called again. Allows a specific exception to be generated when this option is missing.
     */
    fun finished(context: ParseContext): FinishResult {
        return FinishResult.Success
    }
}