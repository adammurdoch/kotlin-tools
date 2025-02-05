package net.rubygrapefruit.cli

/**
 * Represents the current state of some parsing.
 */
internal interface ParseState {
    /**
     * Attempts to parse the next value from the given inputs.
     */
    fun parseNextValue(args: List<String>): Result

    /**
     * Attempts to parse the end of the input.
     */
    fun endOfInput(): FinishResult

    sealed interface Result

    /**
     * Did not recognize anything. The state can be reused.
     */
    data object Nothing : Result

    /**
     * Parsing has recognized a value and will not match any more inputs.
     * The state should be discarded and the given function called to apply the result of parsing.
     */
    data class Success(val consumed: Int, val hint: FailureHint? = null, val apply: () -> Unit) : Result

    /**
     * Parsing has recognized a value and can continue parsing more values.
     * The state should be replaced by the given state to parse the next value.
     */
    data class Continue(val consumed: Int, val state: ParseState, val hint: FailureHint? = null, val apply: () -> Unit) : Result

    /**
     * Recognized a value but has failed and will not match any more inputs.
     * The state should be discarded.
     */
    data class Failure(
        val recognized: Int,
        val message: String,
        val resolution: String? = null,
        val positional: List<PositionalUsage> = emptyList(),
        val actions: List<NamedNestedActionUsage> = emptyList(),
        val expectedMore: Boolean = false
    ) : Result {
        val exception: ArgParseException
            get() {
                return if (positional.isEmpty()) {
                    ArgParseException(message)
                } else {
                    PositionalParseException(message, resolution = resolution ?: message, positional = positional, actions = actions)
                }
            }
    }

    sealed interface FinishResult

    data class FinishSuccess(val apply: () -> Unit) : FinishResult

    data class FinishFailure(
        val message: String,
        val resolution: String? = null,
        val positional: List<PositionalUsage> = emptyList(),
        val actions: List<NamedNestedActionUsage> = emptyList()
    ) : FinishResult {
        fun toResult() = Failure(0, message = message, resolution = resolution, positional = positional, actions = actions, expectedMore = true)

        val exception: ArgParseException
            get() {
                return if (resolution == null) {
                    ArgParseException(message)
                } else {
                    PositionalParseException(message, resolution = resolution, positional = positional, actions = actions)
                }
            }
    }
}
