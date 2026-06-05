package net.rubygrapefruit.parse

/**
 * Encapsulates what is expected next in the input stream, and how to produce a result from this.
 *
 * Implementations may have mutable state and must not be reused.
 */
internal interface PullParser<in IN> : ParseState<IN> {
    /**
     * Forces this parser to stop at the current position.
     */
    fun stop(): Failed

    /**
     * Attempts to parse the given inputs, up to the given max number of values.
     *
     * @param max May be 0
     */
    fun parse(input: IN, max: Int): Result<IN>

    sealed interface Result<in IN>

    sealed interface Finished<in IN> : Result<IN>, ParseState<IN>

    /**
     * Parser has successfully matched
     */
    data object Matched : Finished<Any?>

    /**
     * @param index Relative to the start of input to [parse]. Can be positive or negative. Must be < max passed to [parse].
     */
    data class Failure(val index: Int, val expected: ExpectationProvider) {
        fun position(base: Position): Position {
            return base + index
        }

        fun map(map: (Expectation) -> Expectation): Failure {
            return Failure(index, expected.map(map))
        }
    }

    /**
     * Parser stopped matching.
     */
    data class Failed(val failures: List<Failure>) : Finished<Any?> {
        /**
         * @param index Relative to the start of input to [parse]. Can be positive or negative. Must be < max passed to [parse].
         */
        constructor(index: Int, expected: ExpectationProvider) : this(listOf(Failure(index, expected)))

        fun map(map: (Expectation) -> Expectation): Failed {
            return Failed(failures.map { it.map(map) })
        }
    }

    /**
     * Move the input forward the given number of values and try again with the given parser.
     *
     * @param advance Move forward the given number of input values. Can be 0. Must be <= max passed to [parse].
     * @param matched Parser has matched and moved to its next parser.
     */
    data class RequireMore<in IN>(
        val advance: Int,
        val matched: Boolean,
        val parser: PullParser<IN>,
        val failedChoices: List<Failure> = emptyList()
    ) : Result<IN>
}
