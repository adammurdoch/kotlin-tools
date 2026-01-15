package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.DefaultPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PullParser

internal class DefaultCharPushParser<OUT>(parser: PullParser<CharStream, OUT>) : DefaultPushParser<CharPosition, AdvancingCharStream, OUT>(parser), CharPushParser<OUT> {
    private val input = BufferingCharStream()

    override fun input(chars: CharArray) {
        if (chars.isEmpty()) {
            return
        }

        input.append(chars)
        inputAvailable(input)
    }

    override fun endOfInput(): ParseResult<CharPosition, OUT> {
        input.end()
        return endOfInput(input)
    }
}