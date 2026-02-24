package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.general.MatchedInputParser

/**
 * Returns a parser that matches a single character and produces the matched character as a result.
 */
fun one(): Parser<CharInput, Char> {
    return OneCharParser
}

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
 * Returns a parser that matches one of the given characters and produces the matched character as a result.
 */
fun oneOf(first: Char, second: Char, vararg additionalChars: Char): Parser<CharInput, Char> {
    return OneOfCharParser.of(listOf(first, second) + additionalChars.toList())
}

/**
 * Returns a parser that matches one of the given characters and produces the matched character as a result.
 */
fun oneOf(chars: Collection<Char>): Parser<CharInput, Char> {
    return OneOfCharParser.of(chars)
}

/**
 * Returns a parser that matches one of the given characters and produces the matched character as a result.
 */
fun oneOf(chars: CharRange): Parser<CharInput, Char> {
    return OneOfCharRangeParser(chars)
}

/**
 * Returns a parser that applies the given parser and produces the input text that it matches.
 */
fun match(parser: Parser<CharInput, *>): Parser<CharInput, String> {
    return MatchedInputParser(discard((parser)))
}