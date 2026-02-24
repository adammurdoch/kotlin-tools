package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ValueProvider
import kotlin.jvm.JvmName

/**
 * Returns a parser that applies the given parser zero or one times. Produces `null` when there is no match.
 */
fun <IN, OUT : Any> optional(parser: Parser<IN, OUT>): Parser<IN, OUT?> {
    return OptionalParser(parser, ValueProvider.Null)
}

/**
 * Returns a parser that applies the given parser zero or one times. Produces the given default value when there is no match.
 */
fun <IN, OUT> optional(parser: Parser<IN, OUT>, defaultValue: OUT): Parser<IN, OUT> {
    return OptionalParser(parser, ValueProvider.of(defaultValue))
}

/**
 * Returns a parser that applies the given parser zero or one times. Produces no result
 */
@JvmName("optionalProduceNothing")
fun <IN> optional(parser: Parser<IN, Unit>): Parser<IN, Unit> {
    return OptionalParser(parser, ValueProvider.Nothing)
}

/**
 * Returns a parser that applies the given parser zero or more times. Produces a list containing the results.
 */
fun <IN, OUT> zeroOrMore(parser: Parser<IN, OUT>): Parser<IN, List<OUT>> {
    return ZeroOrMoreParser(parser)
}

/**
 * Returns a parser that applies the given parser zero or more times. Does not produce a result.
 */
@JvmName("zeroOrMoreProduceNothing")
fun <IN> zeroOrMore(parser: Parser<IN, Unit>): Parser<IN, Unit> {
    return ZeroOrMoreProduceNothingParser(parser)
}

/**
 * Returns a parser that applies the given parser one or more times. Produces a list containing the results.
 */
fun <IN, OUT> oneOrMore(parser: Parser<IN, OUT>): Parser<IN, List<OUT>> {
    return OneOrMoreParser(parser)
}

/**
 * Returns a parser that applies the given parser one or more times. Produces no result
 */
@JvmName("oneOrMoreProduceNothing")
fun <IN> oneOrMore(parser: Parser<IN, Unit>): Parser<IN, Unit> {
    return OneOrMoreProduceNothingParser(parser)
}