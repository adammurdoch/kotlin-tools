package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.PullParser

internal class ByteLiteralParser<OUT>(private val bytes: ByteArray, result: OUT) : Parser<ByteInput, OUT>, PullParser<ByteStream, OUT> {
    private val fail = PullParser.Failed<ByteStream, OUT>(0)
    private val success = PullParser.Matched<ByteStream, OUT>(bytes.size, result)
    private val requireMore = PullParser.RequireMore(this)

    override fun parse(input: ByteStream): PullParser.Result<ByteStream, OUT> {
        for (index in bytes.indices) {
            if (index >= input.length) {
                return requireMore
            }
            if (input.get(index) != bytes[index]) {
                return fail
            }
        }
        return success
    }
}