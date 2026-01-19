package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser
import kotlin.jvm.JvmName

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