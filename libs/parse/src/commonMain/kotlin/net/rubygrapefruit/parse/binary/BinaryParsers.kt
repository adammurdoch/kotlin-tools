package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.not
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.general.MatchOneInputParser
import net.rubygrapefruit.parse.general.MatchedInputParser

/**
 * Returns a parser that matches a single byte and produces the matched byte as a result.
 */
fun one(): Parser<BinaryInput, Byte> {
    return MatchOneInputParser(AnyBytePredicate, Expectation.One("any byte"))
}

/**
 * Returns a parser that matches the given byte. Does not produce a result.
 */
fun literal(byte: Byte): Parser<BinaryInput, Unit> {
    return literal(byteArrayOf(byte))
}

/**
 * Returns a parser that matches the given byte and produces the given result.
 */
fun <OUT> literal(byte: Byte, result: OUT): Parser<BinaryInput, OUT> {
    return literal(byteArrayOf(byte), result)
}

/**
 * Returns a parser that matches the given sequence of bytes. Does not produce a result.
 */
fun literal(bytes: ByteArray): Parser<BinaryInput, Unit> {
    return BinaryLiteralParser(bytes, Unit)
}

/**
 * Returns a parser that matches the given sequence of bytes and produces the given result.
 */
fun <OUT> literal(bytes: ByteArray, result: OUT): Parser<BinaryInput, OUT> {
    return BinaryLiteralParser(bytes, result)
}

/**
 * Returns a parser that matches one of the given bytes and produces the matched byte as a result.
 */
fun oneOf(first: Byte, second: Byte, vararg additional: Byte): Parser<BinaryInput, Byte> {
    return oneOf(listOf(first, second) + additional.toList())
}

/**
 * Returns a parser that matches one of the given bytes and produces the matched byte as a result.
 */
fun oneOf(bytes: Collection<Byte>): Parser<BinaryInput, Byte> {
    val effective = bytes.distinct()
    if (effective.size < 2) {
        throw IllegalArgumentException("2 or more bytes required.")
    }

    val expectation = Expectation.oneOf(effective.map { Expectation.One(format(it)) })
    return MatchOneInputParser(OneOfBytePredicate(effective.toByteArray()), expectation)
}

/**
 * Returns a parser that matches a byte in the given range and produces the matched byte as a result.
 *
 * @param from inclusive
 * @param to inclusive
 */
fun oneInRange(from: Byte, to: Byte): Parser<BinaryInput, Byte> {
    return MatchOneInputParser(ByteInRangePredicate(from, to), Expectation.One("${format(from)}..${format(to)}"))
}

/**
 * Returns a parser that matches any single byte except when the given parser succeeds.
 * Produces the matched byte as a result.
 */
fun oneExcept(parser: Parser<BinaryInput, *>): Parser<BinaryInput, Byte> {
    return sequence(not(parser), one())
}

/**
 * Returns a parser that applies the given parser and produces the input bytes that it matched.
 */
fun match(parser: Parser<BinaryInput, *>): Parser<BinaryInput, ByteArray> {
    return MatchedInputParser(discard(parser))
}