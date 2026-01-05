package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Parser

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
