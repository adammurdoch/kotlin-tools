package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.*

internal class OneOfByteParser(private val bytes: ByteArray) : Parser<ByteInput, Byte>, ParserBuilder<ByteStream, Byte>, SingleInputParser<ByteStream, Byte> {
    override val expectation = Expectation.OneOf(bytes.map { Expectation.One(format(it)) })

    override fun match(input: ByteStream, index: Int): Boolean {
        return bytes.contains(input.get(index))
    }

    override fun <NEXT> start(next: ParseContinuation<ByteStream, Byte, NEXT>): PullParser<ByteStream, NEXT> {
        return OneOfBytePullParser(bytes, expectation, next)
    }

    private class OneOfBytePullParser<NEXT>(
        private val bytes: ByteArray,
        override val expectation: Expectation,
        private val next: ParseContinuation<ByteStream, Byte, NEXT>
    ) : PullParser<ByteStream, NEXT> {

        override fun toString(): String {
            return "{one-of ${bytes.joinToString { format(it) }}}"
        }

        override fun parse(input: ByteStream, max: Int): PullParser.Result<ByteStream, NEXT> {
            return if (max == 0) {
                if (input.finished) {
                    PullParser.Failed(0, expectation)
                } else {
                    PullParser.RequireMore(0, this)
                }
            } else {
                val byte = input.get(0)
                if (bytes.contains(byte)) {
                    next.matched(0, 1, byte)
                } else {
                    PullParser.Failed(0, expectation)
                }
            }
        }
    }
}