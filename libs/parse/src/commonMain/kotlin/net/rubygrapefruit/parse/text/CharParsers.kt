package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.general.MatchedInputParser

/**
 * Returns a parser that matches the given text. Does not produce a result.
 */
fun literal(text: String): Parser<CharInput, Unit> {
    return CharLiteralParser(text, Unit)
}

/**
 * Returns a parser that matches the given text and produces the given result.
 */
fun <OUT> literal(text: String, result: OUT): Parser<CharInput, OUT> {
    return CharLiteralParser(text, result)
}

/**
 * Returns a parser that matches one of the given characters and produces it as a result.
 */
fun oneOf(vararg chars: Char): Parser<CharInput, Char> {
    return OneOfCharParser(chars)
}

/**
 * Returns a parser that applies the given parser and produces the input text that it matched.
 */
fun match(parser: Parser<CharInput, *>): Parser<CharInput, String> {
    return MatchedInputParser(parser)
}