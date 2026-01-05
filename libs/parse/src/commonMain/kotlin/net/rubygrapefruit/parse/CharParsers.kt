package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.CharLiteralParser

/**
 * Returns a parser that matches the given text. Does not produce a value.
 */
fun literal(text: String): Parser<CharStream, Unit> {
    return CharLiteralParser(text)
}
