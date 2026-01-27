package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.*

internal class CharLiteralParser<OUT>(
    private val text: String, val result: OUT
) : Parser<CharInput, OUT>, ParserBuilder<CharStream, OUT>, DiscardableParser<CharInput> {
    override val expectation = Expectation.One(format(text))

    override fun withNoResult(): Parser<CharInput, Unit> {
        return CharLiteralParser(text, Unit)
    }

    override fun <NEXT> start(next: ParseContinuation<CharStream, OUT, NEXT>): PullParser<CharStream, NEXT> {
        return CharLiteralPullParser(text, result, expectation, next)
    }

    private class CharLiteralPullParser<OUT, NEXT>(
        private val text: String,
        private val result: OUT,
        private val startExpectation: Expectation,
        private val next: ParseContinuation<CharStream, OUT, NEXT>,
    ) : PullParser<CharStream, NEXT> {
        private var matched = 0

        override val expectation: Expectation
            get() = if (matched == 0) startExpectation else Expectation.Nothing

        override fun toString(): String {
            return "{literal ${format(text)} matched=$matched}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed(-matched, startExpectation)
        }

        override fun parse(input: CharStream, max: Int): PullParser.Result<CharStream, NEXT> {
            var index = 0
            val remaining = text.length - matched
            while (index < remaining) {
                if (index >= max) {
                    return if (index >= input.available && input.finished) {
                        PullParser.Failed(-matched, startExpectation)
                    } else {
                        matched += index
                        PullParser.RequireMore(index, this)
                    }
                }
                if (input.get(index) != text[matched + index]) {
                    return PullParser.Failed(-matched, startExpectation)
                }
                index++
            }
            return next.matched(-matched, index, result)
        }
    }
}