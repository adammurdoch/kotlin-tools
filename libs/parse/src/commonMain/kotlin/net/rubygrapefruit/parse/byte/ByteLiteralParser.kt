package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.PullParser

internal class ByteLiteralParser<OUT>(private val bytes: ByteArray, result: OUT) : Parser<ByteInput, OUT>, PullParser<ByteStream, OUT> {
    private val success = PullParser.Matched<ByteStream, OUT>(bytes.size, result)
    private val requireMore = PullParser.RequireMore(this)

    override fun parse(input: ByteStream): PullParser.Result<ByteStream, OUT> {
        for (index in bytes.indices) {
            if (index >= input.length) {
                return requireMore
            }
            if (input.get(index) != bytes[index]) {
                return PullParser.Failed(index, listOf(format(bytes[index])))
            }
        }
        return success
    }

    override fun endOfInput(input: ByteStream): PullParser.Finished<ByteStream, OUT> {
        return PullParser.Failed(input.length, listOf(format(bytes[input.length])))
    }

    private fun format(byte: Byte): String {
        return 'x' + byte.toString(16).padStart(2, '0')
    }
}