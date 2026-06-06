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
    fun stop(input: IN): Failed

    /**
     * Attempts to parse the given inputs, up to the given max number of values.
     *
     * @param max May be 0
     */
    fun parse(input: IN, max: Int): Result<IN>

    sealed interface Result<in IN> {
        /**
         * Forces parsing to stop at the current location, if not already stopped.
         */
        fun stop(input: IN): Failed

        /**
         * Forces parsing to continue, even if stopped.
         */
        fun parser(): PullParser<IN>
    }

    sealed interface Continuing<in IN>: Result<IN>

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
    data class Failed(val failures: List<Failure>) : Result<Any?>, ParseState<Any?> {
        private val parser = FinishedPullParser(this)

        /**
         * @param index Relative to the start of input to [parse]. Can be positive or negative. Must be < max passed to [parse].
         */
        constructor(index: Int, expected: ExpectationProvider) : this(listOf(Failure(index, expected)))

        override fun parser(): PullParser<Any?> {
            return parser
        }

        override fun stop(input: Any?): Failed {
            return this
        }

        fun map(map: (Expectation) -> Expectation): Failed {
            return Failed(failures.map { it.map(map) })
        }
    }

    private class FinishedPullParser<in IN>(val state: Result<IN>) : PullParser<IN> {
        override fun stop(input: IN): Failed {
            return state.stop(input)
        }

        override fun parse(input: IN, max: Int): Result<IN> {
            return state
        }
    }

    /**
     * Parser has successfully matched.
     * Move the input forward the given number of values and continue with the tail.
     */
    data class Matched<in IN>(
        val advance: Int,
        val parser: PullParser<IN>,
        val failedChoices: List<Failure> = emptyList()
    ) : Result<IN>, ParseState<IN>, Continuing<IN> {
        override fun stop(input: IN): Failed {
            return parser.stop(input)
        }

        override fun parser(): PullParser<IN> {
            return parser
        }
    }

    /**
     * Parser has not matched yet.
     * Move the input forward the given number of values and try again with the given parser.
     *
     * @param advance Move forward the given number of input values. Can be 0. Must be <= max passed to [parse].
     */
    data class RequireMore<in IN>(
        val advance: Int,
        val parser: PullParser<IN>,
        val failedChoices: List<Failure> = emptyList()
    ) : Result<IN>, Continuing<IN> {
        override fun stop(input: IN): Failed {
            return parser.stop(input)
        }

        override fun parser(): PullParser<IN> {
            return parser
        }
    }
}
