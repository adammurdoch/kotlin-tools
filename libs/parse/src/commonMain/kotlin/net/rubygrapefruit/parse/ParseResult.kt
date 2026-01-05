package net.rubygrapefruit.parse

sealed interface ParseResult<out OUT> {
    class Success<OUT>(val value: OUT): ParseResult<OUT>
    class Fail: ParseResult<Nothing>
}