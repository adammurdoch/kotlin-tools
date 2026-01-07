package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class ByteLiteralParser<OUT>(private val bytes: ByteArray, private val result: OUT) : Parser<ByteInput, OUT>, ParserBuilder<ByteStream, OUT> {
    override fun <NEXT> build(next: (PullParser.Matched<ByteStream, OUT>) -> PullParser.Result<ByteStream, NEXT>): PullParser<ByteStream, NEXT> {
        return ByteLiteralPullParser(bytes, result, next)
    }

    private class ByteLiteralPullParser<OUT, NEXT>(
        private val bytes: ByteArray,
        result: OUT,
        private val next: (PullParser.Matched<ByteStream, OUT>) -> PullParser.Result<ByteStream, NEXT>
    ) : PullParser<ByteStream, NEXT> {
        private val success = PullParser.Matched<ByteStream, OUT>(bytes.size, result)
        private val requireMore = PullParser.RequireMore(0, this)

        override fun parse(input: ByteStream): PullParser.Result<ByteStream, NEXT> {
            for (index in bytes.indices) {
                if (index >= input.available) {
                    return if (input.finished) {
                        PullParser.Failed(index, listOf(format(bytes[index])))
                    } else {
                        requireMore
                    }
                }
                if (input.get(index) != bytes[index]) {
                    return PullParser.Failed(index, listOf(format(bytes[index])))
                }
            }
            return next(success)
        }

        private fun format(byte: Byte): String {
            return 'x' + byte.toString(16).padStart(2, '0')
        }
    }
}