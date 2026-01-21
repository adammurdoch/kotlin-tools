package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal class OneOfByteParser(private val bytes: ByteArray) : Parser<ByteInput, Byte>, SingleInputParser<ByteStream, Byte> {
    override val expectation = Expectation.OneOf(bytes.map { Expectation.One(format(it)) })

    override fun toString(): String {
        return "{one-of ${bytes.joinToString { format(it) }}}"
    }

    override fun match(input: ByteStream, index: Int): Boolean {
        return bytes.contains(input.get(index))
    }
}