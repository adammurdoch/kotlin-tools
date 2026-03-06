package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.sequence

/**
 * Returns a parser that parses a 16 bit unsigned int in little endian order.
 */
fun uint16LittleEndian(): Parser<BinaryInput, UShort> {
    return sequence(one(), one()) { b1, b2 -> b2.toUByte().toUShort().rotateLeft(8).or(b1.toUByte().toUShort()) }
}

/**
 * Returns a parser that parses a 16 bit unsigned int in big endian order.
 */
fun uint16BigEndian(): Parser<BinaryInput, UShort> {
    return sequence(one(), one()) { b1, b2 -> b1.toUByte().toUShort().rotateLeft(8).or(b2.toUByte().toUShort()) }
}