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

    override fun endOfInput(): ParseResult<TextFailureContext, OUT> {
        input.end()
        return endOfInput(input)
    }
}