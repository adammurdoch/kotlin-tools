package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.AbstractPushParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PullParser

internal class DefaultCharPushParser<OUT>(parser: PullParser<CharStream, OUT>) : AbstractPushParser<CharPosition, CharStream, OUT>(parser), CharPushParser<OUT> {
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