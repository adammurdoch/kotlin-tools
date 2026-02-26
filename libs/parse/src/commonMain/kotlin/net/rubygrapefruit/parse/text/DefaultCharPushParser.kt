package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.DefaultPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PullParser

internal class DefaultCharPushParser<OUT>(parser: PullParser<CharStream, OUT>) : DefaultPushParser<CharFailureContext, AdvancingCharStream, OUT>(parser), CharPushParser<OUT> {
    private val input = BufferingCharStream()

    override fun input(chars: CharArray, offset: Int, count: Int) {
        if (count == 0) {
            return
        }

        input.append(chars, offset, count)
        inputAvailable(input)
    }

    override fun endOfInput(): ParseResult<CharFailureContext, OUT> {
        input.end()
        return endOfInput(input, ::failureFactory)
    }
}