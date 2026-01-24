package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.combinators.Extractor

internal class SingleInputCompiledParser<IN : Input<*>, OUT>(
    val parser: SingleInputParser<IN>,
    val extractor: Extractor<IN, OUT>
) : CompiledParser<IN, OUT> {
    override val mayNotAdvanceOnMatch: Boolean
        get() = false

    override val expectation: Expectation
        get() = parser.expectation

    override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return SingleInputPullParser(parser, extractor, next)
    }

    private class SingleInputPullParser<IN : Input<*>, OUT, NEXT>(
        private val parser: SingleInputParser<IN>,
        private val extractor: Extractor<IN, OUT>,
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
                    val result = extractor.extract(input)
                    next.matched(0, 1, result)
                } else {
                    PullParser.Failed(0, expectation)
                }
            }
        }
    }
}