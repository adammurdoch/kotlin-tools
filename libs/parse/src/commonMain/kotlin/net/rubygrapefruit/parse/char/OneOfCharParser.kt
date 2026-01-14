package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.*

internal class OneOfCharParser(private val chars: CharArray) : Parser<CharInput, Char>, ParserBuilder<CharStream, Char> {
    override val expectation = Expectation.OneOf(chars.map { Expectation.One(format(it)) })

    override fun <NEXT> start(next: ParseContinuation<CharStream, Char, NEXT>): PullParser<CharStream, NEXT> {
        return OneOfCharPullParser(chars, expectation, next)
    }

    private class OneOfCharPullParser<NEXT>(
        private val chars: CharArray,
        override val expected: Expectation,
        private val next: ParseContinuation<CharStream, Char, NEXT>
    ) : PullParser<CharStream, NEXT> {
        override fun parse(input: CharStream, max: Int): PullParser.Result<CharStream, NEXT> {
            return if (max == 0) {
                if (input.finished) {
                    PullParser.Failed(0, expected)
                } else {
                    PullParser.RequireMore(0, this)
                }
            } else {
                val ch = input.get(0)
                if (chars.contains(ch)) {
                    next.matched(1, ch)
                } else {
                    PullParser.Failed(0, expected)
                }
            }
        }
    }
}