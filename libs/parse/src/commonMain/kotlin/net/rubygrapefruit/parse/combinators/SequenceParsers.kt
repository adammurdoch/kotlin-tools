package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser
import kotlin.jvm.JvmName

/*
 * 2 PART SEQUENCES
 */

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
    return sequence(discard(prefix), parser)
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
    return sequence(parser, discard(suffix))
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
fun <IN> sequence(a: Parser<IN, Unit>, b: Parser<IN, Unit>, vararg additional: Parser<IN, Unit>): Parser<IN, Unit> {
    return if (additional.isEmpty()) {
        sequence(a, b) { _, _ -> }
    } else {
        var tail = additional.last()
        for (parser in additional.reversed().drop(1)) {
            tail = sequence(parser, tail) { _, _ -> }
        }
        sequence(a, b, tail) { _, _, _ -> }
    }
}

/*
 * 3 PART SEQUENCES
 */

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result.
 */
fun <IN, A, B, C, OUT> sequence(a: Parser<IN, A>, b: Parser<IN, B>, c: Parser<IN, C>, map: (A, B, C) -> OUT): Parser<IN, OUT> {
    val tail = Sequence2Parser(b, c) { b, c -> Pair(b, c) }
    return Sequence2Parser(a, tail) { a, tail -> map(a, tail.first, tail.second) }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the results of the second and third parsers.
 */
fun <IN, A, B, OUT> prefixed(prefix: Parser<IN, *>, a: Parser<IN, A>, b: Parser<IN, B>, map: (A, B) -> OUT): Parser<IN, OUT> {
    return sequence(discard(prefix), a, b, map)
}

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the results of the second and third parsers.
 */
@JvmName("prefixSequence")
fun <IN, A, B, OUT> sequence(prefix: Parser<IN, Unit>, a: Parser<IN, A>, b: Parser<IN, B>, map: (A, B) -> OUT): Parser<IN, OUT> {
    return sequence(prefix, a, b) { _, a, b -> map(a, b) }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the middle parser.
 */
fun <IN, OUT> quoted(prefix: Parser<IN, *>, parser: Parser<IN, OUT>, suffix: Parser<IN, *>): Parser<IN, OUT> {
    return sequence(discard(prefix), parser, discard(suffix))
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the middle parser.
 */
@JvmName("quotedSequence")
fun <IN, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, OUT>, suffix: Parser<IN, Unit>): Parser<IN, OUT> {
    return sequence(discard(prefix), parser, discard(suffix)) { _, b, _ -> b }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of applying the given mapping function to the result of the first and last parsers.
 */
fun <IN, A, B, OUT> separated(a: Parser<IN, A>, separator: Parser<IN, *>, b: Parser<IN, B>, map: (A, B) -> OUT): Parser<IN, OUT> {
    return sequence(a, discard(separator), b, map)
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of applying the given mapping function to the result of the first and last parsers.
 */
fun <IN, A, B, OUT> sequence(a: Parser<IN, A>, separator: Parser<IN, Unit>, b: Parser<IN, B>, map: (A, B) -> OUT): Parser<IN, OUT> {
    return sequence(a, separator, b) { a, _, b -> map(a, b) }
}

/*
 * 4 PART SEQUENCES
 */

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result.
 */
fun <IN, A, B, C, D, OUT> sequence(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    map: (A, B, C, D) -> OUT
): Parser<IN, OUT> {
    val tail1 = Sequence2Parser(c, d) { c, d -> Pair(c, d) }
    val tail2 = Sequence2Parser(b, tail1) { b, tail -> Pair(b, tail) }
    return Sequence2Parser(a, tail2) { a, tail -> map(a, tail.first, tail.second.first, tail.second.second) }
}

/*
 * 5 PART SEQUENCES
 */

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result.
 */
fun <IN, A, B, C, D, E, OUT> sequence(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    e: Parser<IN, E>,
    map: (A, B, C, D, E) -> OUT
): Parser<IN, OUT> {
    val tail1 = Sequence2Parser(d, e) { d, e -> Pair(d, e) }
    val tail2 = Sequence2Parser(c, tail1) { c, tail -> Pair(c, tail) }
    val tail3 = Sequence2Parser(b, tail2) { b, tail -> Pair(b, tail) }
    return Sequence2Parser(a, tail3) { a, tail -> map(a, tail.first, tail.second.first, tail.second.second.first, tail.second.second.second) }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of applying the given mapping function to the result of the first, third and last parsers.
 */
fun <IN, A, B, C, OUT> separated(
    a: Parser<IN, A>,
    separator1: Parser<IN, *>,
    b: Parser<IN, B>,
    separator2: Parser<IN, *>,
    c: Parser<IN, C>,
    map: (A, B, C) -> OUT
): Parser<IN, OUT> {
    return sequence(a, discard(separator1), b, discard(separator2), c) { a, _, b, _, c -> map(a, b, c) }
}
