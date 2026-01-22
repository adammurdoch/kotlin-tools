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
     */
    data class Matched<out OUT>(val value: OUT) : Finished<Any?, OUT>

    /**
     * Parser stopped matching
     *
     * @param index Relative to the start of input to [parse]. Can be positive or negative. Must be < max passed to [parse].
     */
    data class Failed(val index: Int, val expected: Expectation) : Finished<Any?, Nothing>

    /**
     * Move the input forward the given number of values and try again.
     *
     * @param advance Can be 0. Must be <= max passed to [parse].
     */
    data class RequireMore<in IN, out OUT>(val advance: Int, val parser: PullParser<IN, OUT>) : Result<IN, OUT>
}
