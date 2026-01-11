package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that applies the given parser zero or more times. Produces a list containing the results.
 */
fun <IN, OUT> zeroOrMore(parser: Parser<IN, OUT>): Parser<IN, List<OUT>> {
    return ZeroOrMoreParser(parser)
}