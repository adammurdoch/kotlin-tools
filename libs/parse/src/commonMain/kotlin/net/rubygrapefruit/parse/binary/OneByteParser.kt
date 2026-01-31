package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal object OneByteParser : Parser<ByteInput, Byte>, SingleInputParser<ByteStream> {
    override val expectation: Expectation = Expectation.One("one byte")

    override fun toString(): String {
        return "{one-byte}"
    }

    override fun match(input: ByteStream, index: Int): Boolean {
        return true
    }
}