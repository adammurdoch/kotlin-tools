package net.rubygrapefruit.parse

/**
 * Encapsulates what is expected next in the input stream, and how to produce a result from this.
 *
 * Implementations may have mutable state and must not be reused.
 */
internal interface PullParser<in IN, out OUT> : ParseState<IN, OUT> {
    /**
     * Forces this parser to stop at the current position.
     */
    fun stop(): Failed

    /**
     * Attempts to parse the given inputs, up to the given max number of values.
     *
     * @param max May be 0
     */
    fun parse(input: IN, max: Int): Result<IN, OUT>

    sealed interface Result<in IN, out OUT>

    sealed interface Finished<in IN, out OUT> : Result<IN, OUT>, ParseState<IN, OUT>

    /**
     * Parser has successfully matched
     */
    data class Matched<out OUT>(val value: OUT) : Finished<Any?, OUT>

    /**
     * Parser stopped matching
     *
     * @param index Relative to the start of input to [parse]. Can be positive or negative. Must be < max passed to [parse].
     */
    data class Failed(val index: Int, val expected: ExpectationProvider) : Finished<Any?, Nothing> {
        fun map(map: (Expectation) -> Expectation): Failed {
            return Failed(index, expected.map(map))
        }

        companion object {
            fun merged(failures: List<Failed>): Failed {
                val largestIndex = failures.maxOf { it.index }
                return Failed(largestIndex, MergedFailures(largestIndex, failures))
            }
        }
    }

    private class MergedFailures(val largestIndex: Int, val failures: List<Failed>) : ExpectationProvider {
        override fun toString(): String {
            return "{merged $failures}"
        }

        override fun expectation(): Expectation {
            val relevantFailures = failures.filter { it.index == largestIndex }
            return Expectation.oneOf(relevantFailures.map { it.expected.expectation() })
        }
    }

    /**
     * Move the input forward the given number of values and try again.
     *
     * @param advance Can be 0. Must be <= max passed to [parse].
     */
    data class RequireMore<in IN, out OUT>(
        val advance: Int,
        val parser: PullParser<IN, OUT>,
        val failedChoice: ExpectationProvider? = null
    ) : Result<IN, OUT>
}
