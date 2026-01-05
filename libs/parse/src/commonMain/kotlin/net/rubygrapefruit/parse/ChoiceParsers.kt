package net.rubygrapefruit.parse

/**
 * Returns a parser that tries to match one of the given parsers. Parsers are (logically) attempted in the order provided.
 */
fun <IN, OUT> oneOf(vararg choices: Parser<IN, OUT>): Parser<IN, OUT> {
    TODO()
}