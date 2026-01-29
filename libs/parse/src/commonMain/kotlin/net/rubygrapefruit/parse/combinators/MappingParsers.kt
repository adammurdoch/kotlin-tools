package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that applies the given parser and maps its result.
 */
fun <IN, INTERMEDIATE, OUT> map(parser: Parser<IN, INTERMEDIATE>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return MapParser(parser, map)
}

/**
 * Returns a parser that applies the given parser but does not produce a result.
 *
 * Semantically, this is the same as `map(parser) { }`.
 * However, the given parser is transformed so that it does not produce any intermediate results.
 * For example, mapping functions are discarded, values are not extracted from the input, lists of values are not created, etc
 */
fun <IN> discard(parser: Parser<IN, *>): Parser<IN, Unit> {
    return DiscardParser(parser)
}

/**
 * Returns a parser that applies the given parser and produces the given result.
 *
 * Semantically, this is the same as `map(parser) { result }`.
 */
fun <IN, OUT> replace(parser: Parser<IN, *>, result: OUT): Parser<IN, OUT> {
    return map(discard(parser)) { result }
}

/**
 * Returns a parser that applies the given parser and calls the given function with its result.
 */
fun <IN, OUT> consume(parser: Parser<IN, OUT>, consumer: (OUT) -> Unit): Parser<IN, Unit> {
    return ConsumeParser(parser, consumer)
}