package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that applies the given parser but does not produce a result.
 *
 * Semantically, this is similar to `map(parser) { }`.
 * However, the given parser is transformed so that it does not produce any intermediate results.
 * For example, mapping functions are discarded, values are not extracted from the input, lists of values are not created, etc
 */
fun <IN> discard(parser: Parser<IN, *>): Parser<IN, Unit> {
    return DiscardParser(parser)
}