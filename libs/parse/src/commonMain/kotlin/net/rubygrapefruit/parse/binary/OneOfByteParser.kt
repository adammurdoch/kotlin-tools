package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal class OneOfByteParser private constructor(val bytes: List<Byte>) : Parser<ByteInput, Byte>, SingleInputParser<ByteStream> {
    override val expectation = Expectation.oneOf(bytes.map { Expectation.One(format(it)) })

    override fun toString(): String {
        return "{one-of ${bytes.joinToString { format(it) }}}"
    }

    override fun match(input: ByteStream, index: Int): Boolean {
        return bytes.contains(input.get(index))
    }

    companion object {
        fun of(bytes: Iterable<Byte>): OneOfByteParser {
            val effective = bytes.distinct()
            if (effective.size < 2) {
                throw IllegalArgumentException("2 or more bytes required.")
            }
            return OneOfByteParser(effective)
        }
    }
}