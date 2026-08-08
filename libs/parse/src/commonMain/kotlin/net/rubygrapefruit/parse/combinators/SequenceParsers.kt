package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.general.SucceedParser
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
fun <IN, INTERMEDIATE, OUT> prefixed(prefix: Parser<IN, *>, parser: Parser<IN, INTERMEDIATE>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return sequence(discard(prefix), parser, map)
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
fun <IN, INTERMEDIATE, OUT> suffixed(parser: Parser<IN, INTERMEDIATE>, suffix: Parser<IN, *>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return sequence(parser, discard(suffix), map)
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
 * Always succeeds and consumes no input when no parser provided.
 */
fun <IN> sequence(vararg parsers: Parser<IN, Unit>): Parser<IN, Unit> {
    return sequence(parsers.toList())
}

/**
 * Returns a parser that applies the given parsers in order. Produces no result.
 * Always succeeds and consumes no input when no parser provided.
 */
fun <IN> sequence(parsers: List<Parser<IN, Unit>>): Parser<IN, Unit> {
    return when (parsers.size) {
        0 -> SucceedParser(Unit)
        1 -> parsers.first()
        else -> {
            var tail = parsers.last()
            for (parser in parsers.reversed().drop(1)) {
                tail = sequence(parser, tail) { _, _ -> }
            }
            tail
        }
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
    return seq3(a, tuple2(b, c), map)
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
    return sequence(prefix, sequence(a, b, map))
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the middle parser.
 */
fun <IN, OUT> surrounded(prefix: Parser<IN, *>, parser: Parser<IN, OUT>, suffix: Parser<IN, *>): Parser<IN, OUT> {
    return sequence(discard(prefix), parser, discard(suffix))
}

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the result of the middle parser.
 */
fun <IN, INTERMEDIATE, OUT> surrounded(prefix: Parser<IN, *>, parser: Parser<IN, INTERMEDIATE>, suffix: Parser<IN, *>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return sequence(discard(prefix), parser, discard(suffix), map)
}

/**
 * Returns a parser that applies the given parsers in order.
 * Produces the result of the middle parser.
 */
@JvmName("quotedSequence")
fun <IN, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, OUT>, suffix: Parser<IN, Unit>): Parser<IN, OUT> {
    return sequence(prefix, sequence(parser, suffix))
}

/**
 * Returns a parser that applies the given parsers in order.
 * Uses the given mapping function to produce the result from the result of the middle parser.
 */
@JvmName("quotedSequence")
fun <IN, INTERMEDIATE, OUT> sequence(prefix: Parser<IN, Unit>, parser: Parser<IN, INTERMEDIATE>, suffix: Parser<IN, Unit>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return sequence(prefix, sequence(parser, suffix) { b, _ -> map(b) })
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
    return sequence(a, sequence(separator, b), map)
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
    return Sequence2Parser(a, tuple3(b, c, d)) { a, tail -> map(a, tail.a, tail.b, tail.c) }
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
    return Sequence2Parser(a, tuple4(b, c, d, e)) { a, tail -> map(a, tail.a, tail.b, tail.c, tail.d) }
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
    return seq3(a, sequence(separator1, tuple2(b, sequence(separator2, c))), map)
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
    return Sequence2Parser(a, tuple5(b, c, d, e, f)) { a, tail -> map(a, tail.a, tail.b, tail.c, tail.d, tail.e) }
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
    return Sequence2Parser(a, tuple6(b, c, d, e, f, g)) { a, tail -> map(a, tail.a, tail.b, tail.c, tail.d, tail.e, tail.f) }
}

/*
 * Tuple parsers.
 */

private fun <IN, A, B> tuple2(
    a: Parser<IN, A>,
    b: Parser<IN, B>
): Parser<IN, Tuple2<A, B>> {
    return Sequence2Parser(a, b) { a, b -> Tuple2(a, b) }
}

private fun <IN, A, B, C> tuple3(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>
): Parser<IN, Tuple3<A, B, C>> {
    return Sequence2Parser(a, tuple2(b, c)) { a, tail -> Tuple3(a, tail) }
}

private fun <IN, A, B, C, OUT> seq3(
    a: Parser<IN, A>,
    tail: Parser<IN, Tuple2<B, C>>,
    map: (A, B, C) -> OUT
): Parser<IN, OUT> {
    return Sequence2Parser(a, tail) { a, tail -> map(a, tail.a, tail.b) }
}

private fun <IN, A, B, C, D> tuple4(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>
): Parser<IN, Tuple4<A, B, C, D>> {
    return Sequence2Parser(a, tuple3(b, c, d)) { a, tail -> Tuple4(a, tail) }
}

private fun <IN, A, B, C, D, E> tuple5(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    e: Parser<IN, E>
): Parser<IN, Tuple5<A, B, C, D, E>> {
    return Sequence2Parser(a, tuple4(b, c, d, e)) { a, tail -> Tuple5(a, tail) }
}

private fun <IN, A, B, C, D, E, F> tuple6(
    a: Parser<IN, A>,
    b: Parser<IN, B>,
    c: Parser<IN, C>,
    d: Parser<IN, D>,
    e: Parser<IN, E>,
    f: Parser<IN, F>
): Parser<IN, Tuple6<A, B, C, D, E, F>> {
    return Sequence2Parser(a, tuple5(b, c, d, e, f)) { a, tail -> Tuple6(a, tail) }
}
