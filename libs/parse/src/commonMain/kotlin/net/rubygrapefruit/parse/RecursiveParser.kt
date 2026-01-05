package net.rubygrapefruit.parse

interface RecursiveParser<IN, OUT> : Parser<IN, OUT> {
    fun parser(parser: Parser<IN, OUT>)
}

fun <IN, OUT> recursive(): RecursiveParser<IN, OUT> {
    TODO()
}