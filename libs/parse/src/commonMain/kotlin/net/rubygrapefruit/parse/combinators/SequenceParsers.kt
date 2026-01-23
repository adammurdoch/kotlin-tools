package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser
import kotlin.jvm.JvmName

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
@JvmName("prefixSequence")
fun <IN, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, OUT>): Parser<IN, OUT> {
    return prefixed(prefix, parser)
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the first parser.
 */
fun <IN, OUT> suffixed(prefix: Parser<IN, OUT>, parser: Parser<IN, *>): Parser<IN, OUT> {
    return Sequence2Parser(prefix, parser) { a, _ -> a }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the first parser.
 */
@JvmName("suffixSequence")
fun <IN, OUT> sequence(prefix: Parser<IN, OUT>, parser: Parser<IN, Unit>): Parser<IN, OUT> {
    return suffixed(prefix, parser)
}
