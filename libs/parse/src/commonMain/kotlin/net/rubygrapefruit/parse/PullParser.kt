package net.rubygrapefruit.parse

internal interface PullParser<in IN, out OUT> {
    fun parse(input: IN): Result<IN, OUT>

    fun endOfInput(input: IN): Finished<IN, OUT>

    sealed interface Result<in IN, out OUT>

    sealed interface Finished<in IN, out OUT> : Result<IN, OUT>

    // Parser has successfully matched
    class Matched<IN, OUT>(val count: Int, val value: OUT) : Finished<IN, OUT>

    // Parser stopped matching
    class Failed<IN, OUT>(val index: Int, val message: String) : Finished<IN, OUT>

    // Parser requires more input
    class RequireMore<IN, OUT>(val parser: PullParser<IN, OUT>) : Result<IN, OUT>
}

