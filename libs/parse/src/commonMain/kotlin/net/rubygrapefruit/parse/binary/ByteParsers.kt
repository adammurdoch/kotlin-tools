package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that matches the given sequence of bytes. Does not produce a result.
 */
fun literal(bytes: ByteArray): Parser<ByteInput, Unit> {
    return ByteLiteralParser(bytes, Unit)
}

/**
 * Returns a parser that matches the given sequence of bytes and produces the given result.
 */
fun <OUT> literal(bytes: ByteArray, result: OUT): Parser<ByteInput, OUT> {
    return ByteLiteralParser(bytes, result)
}

/**
 * Returns a parser that matches one of the given bytes and produces it as a result.
 */
fun oneOf(vararg bytes: Byte): Parser<ByteInput, Byte> {
    return OneOfByteParser(bytes)
}