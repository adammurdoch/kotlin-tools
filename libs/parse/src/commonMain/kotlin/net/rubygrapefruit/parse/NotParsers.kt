package net.rubygrapefruit.parse

/**
 * Returns a parser that succeeds when the given parser fails, and vice versa.
 * Does not produce a value or consume any input.
 */
fun <IN> not(parser: Parser<IN, Unit>): Parser<IN, Unit> {
    return NotParser(parser)
}