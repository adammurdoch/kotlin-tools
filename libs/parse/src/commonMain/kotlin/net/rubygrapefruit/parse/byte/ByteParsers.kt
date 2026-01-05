package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that matches the given sequence of bytes. Does not produce a value.
 */
fun literal(vararg bytes: Byte): Parser<ByteInput, Unit> {
    return ByteLiteralParser(bytes)
}
