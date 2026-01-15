package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.DefaultPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PullParser

internal class DefaultBytePushParser<OUT>(parser: PullParser<ByteStream, OUT>) : DefaultPushParser<BytePosition, AdvancingByteStream, OUT>(parser), BytePushParser<OUT> {
    private val input = BufferingByteStream()

    override fun input(bytes: ByteArray) {
        if (bytes.isEmpty()) {
            return
        }
        input.append(bytes)
        inputAvailable(input)
    }

    override fun endOfInput(): ParseResult<BytePosition, OUT> {
        input.end()
        return endOfInput(input)
    }
}