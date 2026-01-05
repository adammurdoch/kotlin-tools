package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that matches the given text. Does not produce a value.
 */
fun literal(text: String): Parser<CharInput, Unit> {
    return CharLiteralParser(text)
}
