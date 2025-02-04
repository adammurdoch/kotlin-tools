package net.rubygrapefruit.cli

/**
 * Represents the current state of some parsing.
 */
internal interface ParseState {
    /**
     * Attempts to parse the next value from the given inputs.
     */
    fun parseNextValue(args: List<String>, context: ParseContext): Result

    /**
     * Attempts to parse the end of the input.
     */
    fun endOfInput(context: ParseContext): FinishResult

    sealed interface Result

    /**
     * Did not recognize anything. The state can be reused.
     */
    data object Nothing : Result

    /**
     * Parsing has recognized a value and will not match any more inputs.
     * The state should be discarded and the given function called to apply the result of parsing.
     */
    data class Success(val consumed: Int, val apply: () -> Unit) : Result

    /**
     * Parsing has recognized a value and can continue parsing more values.
     * The state should be replaced by the given state to parse the next value.
     */
    data class Continue(val consumed: Int, val state: ParseState) : Result

    /**
     * Recognized a value but has failed and will not match any more inputs.
     * The state should be discarded.
     */
    data class Failure(val recognized: Int, val message: String) : Result

    sealed interface FinishResult

    data class FinishSuccess(val apply: () -> Unit) : FinishResult
    data class FinishFailure(val message: String) : FinishResult
}
