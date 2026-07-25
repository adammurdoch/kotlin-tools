package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.DefaultPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser

internal class DefaultTextPushParser<OUT>(
    parser: Parser<TextInput, OUT>,
    failureFormatter: (TextFailureContext, String) -> String
) : DefaultPushParser<TextFailureContext, AdvancingCharStream, OUT>(parser, failureFormatter), TextPushParser<OUT> {
    private val input = BufferingCharStream()

    override fun input(chars: CharArray, offset: Int, count: Int): ParseResult.Fail<TextFailureContext>? {
        if (count == 0) {
            return maybeFailed()
        }

        input.append(chars, offset, count)
        return inputAvailable(input)
    }

    /**
     * Parses input provided by the given function. Calls the function when more input is required. The function should return the number of chars read into the array or -1
     * on end of input.
     */
    override fun takeFrom(reader: (buffer: CharArray, offset: Int, max: Int) -> Int): ParseResult.Fail<TextFailureContext>? {
        val buffer = CharArray(16 * 1024)
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

    override fun endOfInput(): ParseResult<TextFailureContext, OUT> {
        input.end()
        return endOfInput(input)
    }
}