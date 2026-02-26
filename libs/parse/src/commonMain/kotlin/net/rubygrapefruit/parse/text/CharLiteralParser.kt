package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.*

internal class CharLiteralParser<OUT>(
    private val text: String, val result: OUT
) : Parser<TextInput, OUT>, ParserBuilder<CharStream, OUT>, DiscardableParser<TextInput> {
    override val expectation = Expectation.One(format(text))
    private val provider = ValueProvider.of(result)

    override fun withNoResult(): Parser<TextInput, Unit> {
        return CharLiteralParser(text, Unit)
    }

    override fun <NEXT> start(next: ParseContinuation<CharStream, OUT, NEXT>): PullParser<CharStream, NEXT> {
        return CharLiteralPullParser(text, provider, expectation, next)
    }

    private class CharLiteralPullParser<OUT, NEXT>(
        private val text: String,
        private val result: ValueProvider<OUT>,
        private val startExpectation: Expectation,
        private val next: ParseContinuation<CharStream, OUT, NEXT>,
    ) : PullParser<CharStream, NEXT> {
        private var matched = 0

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
                        stop()
                    } else {
                        matched += index
                        PullParser.RequireMore(index, 0, false, this)
                    }
                }
                if (input.get(index) != text[matched + index]) {
                    return stop()
                }
                index++
            }
            val parser = next.next(text.length, result)
            return PullParser.RequireMore(index, text.length, next.matches, parser)
        }
    }
}