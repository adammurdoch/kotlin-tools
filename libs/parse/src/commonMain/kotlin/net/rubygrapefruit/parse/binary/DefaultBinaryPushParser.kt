package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.DefaultPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser

internal class DefaultBinaryPushParser<OUT>(
    parser: Parser<BinaryInput, OUT>,
    failureFormatter: (BinaryFailureContext, String) -> String
) : DefaultPushParser<BinaryFailureContext, AdvancingByteStream, OUT>(parser, failureFormatter), BinaryPushParser<OUT> {
    private val input = BufferingByteStream()

    override fun input(bytes: ByteArray, offset: Int, count: Int): ParseResult.Fail<BinaryFailureContext>? {
        if (count == 0) {
            return maybeFailed()
        }
        input.append(bytes, offset, count)
        return inputAvailable(input)
    }

    override fun endOfInput(): ParseResult<BinaryFailureContext, OUT> {
        input.end()
        return endOfInput(input)
    }
}