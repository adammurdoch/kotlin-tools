package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser
import kotlin.jvm.JvmName

/*
 * 2 PART SEQUENCES
 */

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the result of the parsers.
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
 * Uses the given mapping function to produce the result from the result of the second parser.
 */
@JvmName("prefixSequence")
fun <IN, INTERMEDIATE, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, INTERMEDIATE>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return sequence(prefix, parser) { _, b -> map(b) }
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
fun <IN, OUT> sequence(parser: Parser<IN, OUT>, suffix: Parser<IN, Unit>): Parser<IN, OUT> {
    return sequence(parser, suffix) { a, _ -> a }
}

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the result of the first parser.
 */
@JvmName("suffixSequence")
fun <IN, INTERMEDIATE, OUT> sequence(parser: Parser<IN, INTERMEDIATE>, suffix: Parser<IN, Unit>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return sequence(parser, suffix) { a, _ -> map(a) }
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
 * Uses the given mapping function to produce the result from the result of the parsers.
 */
fun <IN, A, B, C, OUT> sequence(a: Parser<IN, A>, b: Parser<IN, B>, c: Parser<IN, C>, map: (A, B, C) -> OUT): Parser<IN, OUT> {
    return Sequence2Parser(a, seq2(b, c)) { a, tail -> map(a, tail.a, tail.b) }
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
 * Uses the given mapping function to produce the result from the result of the middle parser.
 */
@JvmName("quotedSequence")
fun <IN, INTERMEDIATE, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, INTERMEDIATE>, suffix: Parser<IN, Unit>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return sequence(discard(prefix), parser, discard(suffix)) { _, b, _ -> map(b) }
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
 * Uses the given mapping function to produce the result from the result of the parsers.
 */
fun <IN, A, B, C, D, OUT> sequence(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    map: (A, B, C, D) -> OUT
): Parser<IN, OUT> {
    return Sequence2Parser(a, seq3(b, c, d)) { a, tail -> map(a, tail.a, tail.b, tail.c) }
}

/*
 * 5 PART SEQUENCES
 */

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the result of the parsers.
 */
fun <IN, A, B, C, D, E, OUT> sequence(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    e: Parser<IN, E>,
    map: (A, B, C, D, E) -> OUT
): Parser<IN, OUT> {
    return Sequence2Parser(a, seq4(b, c, d, e)) { a, tail -> map(a, tail.a, tail.b, tail.c, tail.d) }
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
    return sequence(a, discard(separator1), b, discard(separator2), c, map)
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of applying the given mapping function to the result of the first, third and last parsers.
 */
fun <IN, A, B, C, OUT> sequence(
    a: Parser<IN, A>,
    separator1: Parser<IN, Unit>,
    b: Parser<IN, B>,
    separator2: Parser<IN, Unit>,
    c: Parser<IN, C>,
    map: (A, B, C) -> OUT
): Parser<IN, OUT> {
    return sequence(a, separator1, b, separator2, c) { a, _, b, _, c -> map(a, b, c) }
}

/*
 * 6 PART SEQUENCES
 */

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the result of the parsers.
 */
fun <IN, A, B, C, D, E, F, OUT> sequence(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    e: Parser<IN, E>,
    f: Parser<IN, F>,
    map: (A, B, C, D, E, F) -> OUT
): Parser<IN, OUT> {
    return Sequence2Parser(a, seq5(b, c, d, e, f)) { a, tail -> map(a, tail.a, tail.b, tail.c, tail.d, tail.e) }
}

/*
 * 7 PART SEQUENCES
 */

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the result of the parsers.
 */
fun <IN, A, B, C, D, E, F, G, OUT> sequence(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    e: Parser<IN, E>,
    f: Parser<IN, F>,
    g: Parser<IN, G>,
    map: (A, B, C, D, E, F, G) -> OUT
): Parser<IN, OUT> {
    return Sequence2Parser(a, seq6(b, c, d, e, f, g)) { a, tail -> map(a, tail.a, tail.b, tail.c, tail.d, tail.e, tail.f) }
}

/*
 * Tuple parsers.
 */

private fun <IN, A, B> seq2(a: Parser<IN, A>, b: Parser<IN, B>): Parser<IN, Tuple2<A, B>> {
    return Sequence2Parser(a, b) { a, b -> Tuple2(a, b) }
}

private fun <IN, A, B, C> seq3(a: Parser<IN, A>, b: Parser<IN, B>, c: Parser<IN, C>): Parser<IN, Tuple3<A, B, C>> {
    return Sequence2Parser(a, seq2(b, c)) { a, tail -> Tuple3(a, tail) }
}

private fun <IN, A, B, C, D> seq4(a: Parser<IN, A>, b: Parser<IN, B>, c: Parser<IN, C>, d: Parser<IN, D>): Parser<IN, Tuple4<A, B, C, D>> {
    return Sequence2Parser(a, seq3(b, c, d)) { a, tail -> Tuple4(a, tail) }
}

private fun <IN, A, B, C, D, E> seq5(a: Parser<IN, A>, b: Parser<IN, B>, c: Parser<IN, C>, d: Parser<IN, D>, e: Parser<IN, E>): Parser<IN, Tuple5<A, B, C, D, E>> {
    return Sequence2Parser(a, seq4(b, c, d, e)) { a, tail -> Tuple5(a, tail) }
}

private fun <IN, A, B, C, D, E, F> seq6(a: Parser<IN, A>, b: Parser<IN, B>, c: Parser<IN, C>, d: Parser<IN, D>, e: Parser<IN, E>, f: Parser<IN, F>): Parser<IN, Tuple6<A, B, C, D, E, F>> {
    return Sequence2Parser(a, seq5(b, c, d, e, f)) { a, tail -> Tuple6(a, tail) }
}
