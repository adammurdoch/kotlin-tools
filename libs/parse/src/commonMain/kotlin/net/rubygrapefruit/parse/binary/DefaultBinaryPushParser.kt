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

    override fun takeFrom(reader: (buffer: ByteArray, offset: Int, max: Int) -> Int): ParseResult.Fail<BinaryFailureContext>? {
        val buffer = ByteArray(16 * 1024)
        while (true) {
            val nread = reader(buffer, 0, buffer.size)
            if (nread < 0) {
                return null
            }
            val failure = input(buffer, 0, nread)
            if (failure != null) {
                return failure
            }
        }
    }

    override fun endOfInput(): ParseResult<BinaryFailureContext, OUT> {
        input.end()
        return endOfInput(input)
    }
}