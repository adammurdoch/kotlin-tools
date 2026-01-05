package net.rubygrapefruit.parse

internal interface ConsumingParser<in IN, out OUT> {
    fun parse(input: IN): Result<OUT>

    sealed interface Result<out OUT> {
        class Success<OUT>(val count: Int, val value: OUT) : Result<OUT>
        class Fail(val count: Int) : Result<Nothing>
    }
}

