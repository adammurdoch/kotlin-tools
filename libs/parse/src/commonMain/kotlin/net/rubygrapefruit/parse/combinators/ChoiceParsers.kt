package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that tries to apply one of the given parsers.
 * Parsers are (logically) attempted in the order provided and uses the result from the first parser that succeeds.
 *
 * When a single parser is given, returns that parser.
 *
 * @throws IllegalArgumentException if no parsers are given.
 */
fun <IN, OUT> oneOf(vararg parsers: Parser<IN, OUT>): Parser<IN, OUT> {
    return oneOfWithImmutableList(parsers.toList())
}

/**
 * Returns a parser that tries to apply one of the given parsers.
 * Parsers are (logically) attempted in the order provided and uses the result from the first parser that succeeds.
 *
 * When a single parser is given, returns that parser.
 *
 * @throws IllegalArgumentException if no parsers are given.
 */
fun <IN, OUT> oneOf(parsers: Collection<Parser<IN, OUT>>): Parser<IN, OUT> {
    return oneOfWithImmutableList(parsers.toList())
}

private fun <IN, OUT> oneOfWithImmutableList(parsers: List<Parser<IN, OUT>>): Parser<IN, OUT> {
    require(parsers.isNotEmpty()) { "At least one parser must be provided." }
    return if (parsers.size == 1) parsers.first() else ChoiceParser(parsers)
}