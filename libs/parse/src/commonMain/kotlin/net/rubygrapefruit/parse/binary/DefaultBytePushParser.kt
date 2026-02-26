package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.DefaultPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PullParser

internal class DefaultBytePushParser<OUT>(parser: PullParser<ByteStream, OUT>) : DefaultPushParser<ByteFailureContext, AdvancingByteStream, OUT>(parser), BytePushParser<OUT> {
    private val input = BufferingByteStream()

    override fun input(bytes: ByteArray, offset: Int, count: Int) {
        if (count == 0) {
            return
        }
        input.append(bytes, offset, count)
        inputAvailable(input)
    }

    override fun endOfInput(): ParseResult<ByteFailureContext, OUT> {
        input.end()
        return endOfInput(input, ::failureFactory)
    }
}