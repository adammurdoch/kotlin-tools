package net.rubygrapefruit.parse

/**
 * Implementations may be mutable.
 */
internal interface PullParser<in IN, out OUT> : ParseState<IN, OUT> {
    /**
     * Attempts to parse the given inputs, up to the given max number of values.
     */
    fun parse(input: IN, max: Int): Result<IN, OUT>

    sealed interface Result<in IN, out OUT>

    sealed interface Finished<in IN, out OUT> : Result<IN, OUT>, ParseState<IN, OUT>

    /**
     * Parser has successfully matched
     *
     * @param count Can be 0.
     */
    class Matched<IN, OUT>(val count: Int, val value: OUT) : Finished<IN, OUT>

    /**
     * Parser stopped matching
     *
     * @param index Relative to the start of input to [parse]. Can be negative.
     */
    class Failed<IN, OUT>(val index: Int, val expected: List<String>) : Finished<IN, OUT>

    /**
     * Move the input forward the given number of values and try again.
     *
     * @param advance Can be 0.
     */
    class RequireMore<IN, OUT>(val advance: Int, val parser: PullParser<IN, OUT>) : Result<IN, OUT>
}
