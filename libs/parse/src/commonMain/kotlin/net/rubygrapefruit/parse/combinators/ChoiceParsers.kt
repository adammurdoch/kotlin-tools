package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that tries to apply one of the given parsers.
 * Parsers are (logically) attempted in the order provided and the result from the first parser that succeeds is used.
 */
fun <IN, OUT> oneOf(vararg choices: Parser<IN, OUT>): Parser<IN, OUT> {
    return ChoiceParser(choices.toList())
}