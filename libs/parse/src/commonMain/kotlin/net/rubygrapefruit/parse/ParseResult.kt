package net.rubygrapefruit.parse

sealed interface ParseResult<out POS, out OUT> {
    class Success<OUT>(val value: OUT) : ParseResult<Nothing, OUT>
    class Fail<POS>(val position: POS, val message: String) : ParseResult<POS, Nothing>
}