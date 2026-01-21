package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*

internal class SingleInputCompiledParser<IN : BoxingInput<*, OUT>, OUT>(
    private val parser: SingleInputParser<IN, OUT>,
) : CompiledParser<IN, OUT> {
    override val mayNotAdvanceOnMatch: Boolean
        get() = false
    override val expectation: Expectation
        get() = parser.expectation

    override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return SingleInputPullParser(parser, next)
    }

    private class SingleInputPullParser<IN : BoxingInput<*, OUT>, OUT, NEXT>(
        private val parser: SingleInputParser<IN, OUT>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        override val expectation: Expectation
            get() = parser.expectation

        override fun toString(): String {
            return "{one $parser}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return if (max == 0) {
                if (input.finished) {
                    PullParser.Failed(0, expectation)
                } else {
                    PullParser.RequireMore(0, this)
                }
            } else {
                if (parser.match(input, 0)) {
                    val byte = input.getBoxed(0)
                    next.matched(0, 1, byte)
                } else {
                    PullParser.Failed(0, expectation)
                }
            }
        }
    }
}