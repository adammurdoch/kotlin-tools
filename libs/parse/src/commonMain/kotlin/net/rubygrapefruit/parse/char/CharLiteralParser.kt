package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.*

internal class CharLiteralParser<OUT>(private val text: String, private val result: OUT) : Parser<CharInput, OUT>, ParserBuilder<CharStream, OUT> {
    private val expectation = Expectation.One(format(text))

    override fun <NEXT> start(next: ParseContinuation<CharStream, OUT, NEXT>): PullParser<CharStream, NEXT> {
        return CharLiteralPullParser(text, result, expectation, next)
    }

    private class CharLiteralPullParser<OUT, NEXT>(
        private val text: String,
        private val result: OUT,
        private val expectation: Expectation,
        private val next: ParseContinuation<CharStream, OUT, NEXT>,
    ) : PullParser<CharStream, NEXT> {
        private var matched = 0

        override fun toString(): String {
            return "{literal \"$text\"}"
        }

        override fun parse(input: CharStream, max: Int): PullParser.Result<CharStream, NEXT> {
            var index = 0
            val remaining = text.length - matched
            while (index < remaining) {
                if (index >= max) {
                    return if (index >= input.available && input.finished) {
                        PullParser.Failed(-matched, expectation)
                    } else {
                        matched += index
                        PullParser.RequireMore(index, this)
                    }
                }
                if (input.get(index) != text[matched + index]) {
                    return PullParser.Failed(-matched, expectation)
                }
                index++
            }
            return next.matched(index, result)
        }
    }
}