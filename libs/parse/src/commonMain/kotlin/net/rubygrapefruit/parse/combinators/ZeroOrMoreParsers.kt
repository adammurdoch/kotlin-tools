package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

fun <IN, OUT> zeroOrMore(parser: Parser<IN, OUT>): Parser<IN, List<OUT>> {
    return ZeroOrMoreParser(parser)
}