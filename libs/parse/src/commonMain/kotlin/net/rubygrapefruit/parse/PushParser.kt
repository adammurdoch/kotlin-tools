package net.rubygrapefruit.parse

interface PushParser<out OUT> {
    fun endOfInput(): ParseResult<OUT>
}