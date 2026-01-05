package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.ByteLiteralParser

/**
 * Returns a parser that matches the given sequence of bytes. Does not produce a value.
 */
fun literal(vararg bytes: Byte): Parser<ByteStream, Unit> {
    return ByteLiteralParser(bytes)
}
