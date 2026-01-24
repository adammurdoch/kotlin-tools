package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that applies the given parser but does not produce a result.
 */
fun <IN> discard(parser: Parser<IN, *>): Parser<IN, Unit> {
    return DiscardParser(parser)
}