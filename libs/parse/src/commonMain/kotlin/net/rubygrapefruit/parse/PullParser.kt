package net.rubygrapefruit.parse

internal interface PullParser<in IN, out OUT> {
    fun parse(input: IN): Result<IN, OUT>

    sealed interface Result<in IN, out OUT>

    // Parser has successfully matched
    class Matched<IN, OUT>(val count: Int, val value: OUT) : Result<IN, OUT>

    // Parser stopped matching
    class Failed<IN, OUT>(val count: Int) : Result<IN, OUT>

    // Parser requires more input
    class RequireMore<IN, OUT>(val parser: PullParser<IN, OUT>) : Result<IN, OUT>
}

