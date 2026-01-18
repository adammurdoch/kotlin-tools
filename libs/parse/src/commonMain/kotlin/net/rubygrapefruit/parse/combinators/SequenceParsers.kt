package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result.
 */
fun <IN, A, B, OUT> sequence(a: Parser<IN, A>, b: Parser<IN, B>, map: (A, B) -> OUT): Parser<IN, OUT> {
    return Sequence2Parser(a, b, map)
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the second parser.
 */
fun <IN, OUT> prefixed(prefix: Parser<IN, *>, parser: Parser<IN, OUT>): Parser<IN, OUT> {
    return Sequence2Parser(prefix, parser) { _, b -> b }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the second parser.
 */
fun <IN, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, OUT>): Parser<IN, OUT> {
    return prefixed(prefix, parser)
}
