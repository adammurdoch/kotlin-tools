package net.rubygrapefruit.parse

/**
 * Encapsulates what is expected next in the input stream, and how to produce a result from this.
 *
 * Implementations may have mutable state and must not be reused.
 */
internal interface PullParser<in IN> : ParseState<IN> {
    /**
     * Forces this parser to stop at the current position.
     *
     * This parser must not be used after calling this method
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

    sealed interface Continuing<in IN> : Result<IN>

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
    sealed class Failed : Result<Any?>, ParseState<Any?> {
        abstract fun failures(): List<Failure>

        override fun parser(): PullParser<Any?> {
            return FinishedPullParser(this)
        }

        override fun stop(input: Any?): Failed {
            return this
        }

        open fun map(map: (Failure) -> Failure): Failed {
            val self = this
            return Lazy { self.failures().map { map(it) } }
        }

        data object None : Failed() {
            override fun map(map: (Failure) -> Failure): Failed {
                return this
            }

            override fun failures(): List<Failure> {
                return emptyList()
            }
        }

        class One(val index: Int, val expected: ExpectationProvider) : Failed() {
            override fun failures(): List<Failure> {
                return listOf(Failure(index, expected))
            }
        }

        class Flatten(val producers: List<Failed>) : Failed() {
            override fun failures(): List<Failure> {
                return producers.flatMap { it.failures() }
            }
        }

        class Lazy(val producer: () -> List<Failure>) : Failed() {
            override fun failures(): List<Failure> {
                return producer()
            }
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
        val failedChoices: Failed = Failed.None
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
        val failedChoices: Failed = Failed.None
    ) : Result<IN>, Continuing<IN> {
        override fun stop(input: IN): Failed {
            return parser.stop(input)
        }

        override fun parser(): PullParser<IN> {
            return parser
        }
    }
}
