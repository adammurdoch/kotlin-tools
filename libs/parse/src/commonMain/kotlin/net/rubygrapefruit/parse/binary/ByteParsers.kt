package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.not
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.general.MatchedInputParser

/**
 * Returns a parser that matches a single byte and produces the matched byte as a result.
 */
fun one(): Parser<ByteInput, Byte> {
    return OneByteParser
}

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
 * Returns a parser that matches one of the given bytes and produces the matched byte as a result.
 */
fun oneOf(first: Byte, second: Byte, vararg additional: Byte): Parser<ByteInput, Byte> {
    return OneOfByteParser.of(listOf(first, second) + additional.toList())
}

/**
 * Returns a parser that matches one of the given bytes and produces the matched byte as a result.
 */
fun oneOf(bytes: Collection<Byte>): Parser<ByteInput, Byte> {
    return OneOfByteParser.of(bytes)
}

/**
 * Returns a parser that matches a byte in the given range and produces the matched byte as a result.
 *
 * @param from inclusive
 * @param to inclusive
 */
fun oneInRange(from: Byte, to: Byte): Parser<ByteInput, Byte> {
    return OneOfByteRangeParser(from, to)
}

/**
 * Returns a parser that matches any single byte except when the given parser succeeds.
 * Produces the matched byte as a result.
 */
fun oneExcept(parser: Parser<ByteInput, *>): Parser<ByteInput, Byte> {
    return sequence(not(parser), one())
}

/**
 * Returns a parser that applies the given parser and produces the input bytes that it matched.
 */
fun match(parser: Parser<ByteInput, *>): Parser<ByteInput, ByteArray> {
    return MatchedInputParser(discard(parser))
}