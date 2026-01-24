package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that succeeds when the given parser fails, and vice versa.
 * Does not produce a result or consume any input.
 */
fun <IN> not(parser: Parser<IN, *>): Parser<IN, Unit> {
    return NotParser(parser)
}