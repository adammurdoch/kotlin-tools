package net.rubygrapefruit.parse

/**
 * Implementations may be mutable.
 */
internal interface PullParser<in IN, out OUT> : ParseState<IN, OUT> {
    /**
     * Attempts to parse the given inputs.
     */
    fun parse(input: IN): Result<IN, OUT>

    sealed interface Result<in IN, out OUT>

    sealed interface Finished<in IN, out OUT> : Result<IN, OUT>, ParseState<IN, OUT>

    // Parser has successfully matched
    class Matched<IN, OUT>(val count: Int, val value: OUT) : Finished<IN, OUT>

    // Parser stopped matching
    class Failed<IN, OUT>(val index: Int, val expected: List<String>) : Finished<IN, OUT>

    // Move the input forward the given number of values and try again
    class RequireMore<IN, OUT>(val advance: Int, val parser: PullParser<IN, OUT>) : Result<IN, OUT>
}
