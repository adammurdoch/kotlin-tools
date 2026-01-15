package net.rubygrapefruit.parse

/**
 * Implementations may have mutable state and must not be reused.
 */
internal interface PullParser<in IN, out OUT> : ParseState<IN, OUT> {
    /**
     * What does this parser currently expect?
     */
    val expectation: Expectation

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
     *
     * @param start Relative to the start of input to [parse]. Must be <= 0.
     * @param end Relative to the start of input to [parse]. Can be 0. Must be smaller that max passed to [parse].
     */
    data class Matched<IN, OUT>(val start: Int, val end: Int, val value: OUT) : Finished<IN, OUT>

    /**
     * Parser stopped matching
     *
     * @param index Relative to the start of input to [parse]. Can be negative.
     */
    data class Failed<IN, OUT>(val index: Int, val expected: Expectation) : Finished<IN, OUT>

    /**
     * Move the input forward the given number of values and try again.
     *
     * @param advance Can be 0.
     */
    data class RequireMore<IN, OUT>(val advance: Int, val parser: PullParser<IN, OUT>) : Result<IN, OUT>
}
