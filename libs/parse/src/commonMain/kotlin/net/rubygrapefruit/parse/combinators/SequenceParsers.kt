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
    return sequence(DiscardParser(prefix), parser)
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the second parser.
 */
@JvmName("prefixSequence")
fun <IN, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, OUT>): Parser<IN, OUT> {
    return sequence(prefix, parser) { _, b -> b }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the first parser.
 */
fun <IN, OUT> suffixed(parser: Parser<IN, OUT>, suffix: Parser<IN, *>): Parser<IN, OUT> {
    return sequence(parser, DiscardParser(suffix))
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the first parser.
 */
@JvmName("suffixSequence")
fun <IN, OUT> sequence(parser: Parser<IN, OUT>, suffixed: Parser<IN, Unit>): Parser<IN, OUT> {
    return sequence(parser, suffixed) { a, _ -> a }
}

/**
 * Returns a parser that applies the given parsers in order. Produces no result.
 */
fun <IN> sequence(a: Parser<IN, Unit>, b: Parser<IN, Unit>): Parser<IN, Unit> {
    return sequence(a, b) { _, _ -> }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result.
 */
fun <IN, A, B, C, OUT> sequence(a: Parser<IN, A>, b: Parser<IN, B>, c: Parser<IN, C>, map: (A, B, C) -> OUT): Parser<IN, OUT> {
    return Sequence2Parser(a, Sequence2Parser(b, c) { b, c -> Pair(b, c) }) { a, tail -> map(a, tail.first, tail.second) }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the middle parser.
 */
fun <IN, OUT> quoted(prefix: Parser<IN, *>, parser: Parser<IN, OUT>, suffix: Parser<IN, *>): Parser<IN, OUT> {
    return sequence(DiscardParser(prefix), parser, DiscardParser(suffix))
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the middle parser.
 */
@JvmName("quotedSequence")
fun <IN, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, OUT>, suffix: Parser<IN, Unit>): Parser<IN, OUT> {
    return sequence(DiscardParser(prefix), parser, DiscardParser(suffix)) { _, b, _ -> b }
}

/**
 * Returns a parser that applies the given parsers in order. Produces no result.
 */
fun <IN> sequence(a: Parser<IN, Unit>, b: Parser<IN, Unit>, c: Parser<IN, Unit>): Parser<IN, Unit> {
    return sequence(a, b, c) { _, _, _ -> }
}