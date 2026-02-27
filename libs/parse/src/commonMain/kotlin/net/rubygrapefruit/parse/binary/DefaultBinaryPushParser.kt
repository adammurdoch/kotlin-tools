package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.DefaultPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PullParser

internal class DefaultBinaryPushParser<OUT>(parser: PullParser<ByteStream, OUT>) : DefaultPushParser<BinaryFailureContext, AdvancingByteStream, OUT>(parser), BinaryPushParser<OUT> {
    private val input = BufferingByteStream()

    override fun input(bytes: ByteArray, offset: Int, count: Int) {
        if (count == 0) {
            return
        }
        input.append(bytes, offset, count)
        inputAvailable(input, ::failureFactory)
    }

    override fun endOfInput(): ParseResult<BinaryFailureContext, OUT> {
        input.end()
        return endOfInput(input, ::failureFactory)
    }
}