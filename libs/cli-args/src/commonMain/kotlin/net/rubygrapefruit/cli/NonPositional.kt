package net.rubygrapefruit.cli

internal interface NonPositional {
    fun usage(): List<NonPositionalUsage>

    /**
     * Attempt to parse the given arguments, returning number of arguments consumed.
     *
     * @param args Is never empty.
     */
    fun accept(args: List<String>, context: ParseContext): ParseResult

    /**
     * Attempts to recognize this positional.
     */
    fun stoppedAt(arg: String): StopResult

    /**
     * Attempt to continue parsing following a parse failure.
     *
     * @param args May be empty.
     */
    fun maybeRecover(args: List<String>, context: ParseContext): Boolean {
        return false
    }

    sealed class StopResult {
        data object Nothing : StopResult()
        data object Recognized : StopResult()
        data class Failure(val failure: ArgParseException) : StopResult()
    }
}