package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.ParseContinuation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class ByteLiteralParser<OUT>(private val bytes: ByteArray, private val result: OUT) : Parser<ByteInput, OUT>, ParserBuilder<ByteStream, OUT> {
    override fun <NEXT> build(next: ParseContinuation<ByteStream, OUT, NEXT>): PullParser<ByteStream, NEXT> {
        return ByteLiteralPullParser(bytes, result, next)
    }

    private class ByteLiteralPullParser<OUT, NEXT>(
        private val bytes: ByteArray,
        private val result: OUT,
        private val next: ParseContinuation<ByteStream, OUT, NEXT>
    ) : PullParser<ByteStream, NEXT> {
        private var matched = 0

        override fun toString(): String {
            return "{literal bytes=${bytes.map { format(it) }}}"
        }

        override fun parse(input: ByteStream, max: Int): PullParser.Result<ByteStream, NEXT> {
            var index = 0
            val remaining = bytes.size - matched
            while (index < remaining) {
                if (index >= max) {
                    return if (index >= input.available && input.finished) {
                        PullParser.Failed(index, listOf(format(bytes[matched + index])))
                    } else {
                        matched += index
                        PullParser.RequireMore(index, this)
                    }
                }
                if (input.get(index) != bytes[matched + index]) {
                    return PullParser.Failed(index, listOf(format(bytes[matched + index])))
                }
                index++
            }
            return next.matched(index, result)
        }

        private fun format(byte: Byte): String {
            return 'x' + byte.toString(16).padStart(2, '0')
        }
    }
}