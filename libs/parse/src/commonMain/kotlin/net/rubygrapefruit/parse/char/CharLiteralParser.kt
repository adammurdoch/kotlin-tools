package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.PullParser

internal class CharLiteralParser<OUT>(private val text: String, result: OUT) : Parser<CharInput, OUT>, PullParser<CharStream, OUT> {
    private val fail = PullParser.Failed<CharStream, OUT>(0, listOf("\"$text\""))
    private val success = PullParser.Matched<CharStream, OUT>(text.length, result)
    private val requireMore = PullParser.RequireMore(this)

    override fun parse(input: CharStream): PullParser.Result<CharStream, OUT> {
        for (index in text.indices) {
            if (index >= input.length) {
                return requireMore
            }
            if (input.get(index) != text[index]) {
                return fail
            }
        }
        return success
    }

    override fun endOfInput(input: CharStream): PullParser.Finished<CharStream, OUT> {
        return fail
    }
}