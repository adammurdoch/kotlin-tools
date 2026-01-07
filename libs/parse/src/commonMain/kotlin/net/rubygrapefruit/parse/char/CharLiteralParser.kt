package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class CharLiteralParser<OUT>(private val text: String, private val result: OUT) : Parser<CharInput, OUT>, ParserBuilder<CharStream, OUT> {
    override fun <NEXT> build(next: (PullParser.Matched<CharStream, OUT>) -> PullParser.Result<CharStream, NEXT>): PullParser<CharStream, NEXT> {
        return CharLiteralPullParser(text, result, next)
    }

    private class CharLiteralPullParser<OUT, NEXT>(
        private val text: String,
        result: OUT,
        private val next: (PullParser.Matched<CharStream, OUT>) -> PullParser.Result<CharStream, NEXT>
    ) : PullParser<CharStream, NEXT> {
        private val fail = PullParser.Failed<CharStream, Nothing>(0, listOf("\"$text\""))
        private val success = PullParser.Matched<CharStream, OUT>(text.length, result)
        private val requireMore = PullParser.RequireMore(0, this)

        override fun parse(input: CharStream): PullParser.Result<CharStream, NEXT> {
            for (index in text.indices) {
                if (index >= input.available) {
                    return if (input.finished) {
                        fail
                    } else {
                        requireMore
                    }
                }
                if (input.get(index) != text[index]) {
                    return fail
                }
            }
            return next(success)
        }
    }
}