package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreSingleInputParser<IN : BoxingInput<*, OUT>, OUT>(private val parser: SingleInputParser<IN, OUT>) : CompiledParser<IN, List<OUT>> {
    override val mayNotAdvanceOnMatch: Boolean
        get() = true

    override val expectation: Expectation
        get() = parser.expectation

    override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
        return ZeroOrMorePullParser(parser, next)
    }

    private class ZeroOrMorePullParser<IN : BoxingInput<*, OUT>, OUT, NEXT>(
        val parser: SingleInputParser<IN, OUT>,
        val next: ParseContinuation<IN, List<OUT>, NEXT>
    ) : PullParser<IN, NEXT> {
        private val result = mutableListOf<OUT>()

        override val expectation: Expectation
            get() = parser.expectation

        override fun toString(): String {
            return "{zero-or-more $parser}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val matched = result.size
            var index = 0
            while (index < max) {
                if (!parser.match(input, index)) {
                    break
                }
                result.add(input.getBoxed(index))
                index++
            }
            return if (index < max || index == input.available && input.finished) {
                val nextParser = next.next(index - matched, result)
                PullParser.RequireMore(index, EndPullParser(nextParser, expectation))
            } else {
                PullParser.RequireMore(index, this)
            }
        }
    }

    private class EndPullParser<IN, OUT>(val parser: PullParser<IN, OUT>, val optionExpectation: Expectation) : PullParser<IN, OUT> {
        override val expectation: Expectation
            get() = Expectation.OneOf.of(optionExpectation, parser.expectation)

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            val result = parser.parse(input, max)
            return when (result) {
                is PullParser.Failed -> {
                    if (result.index == 0) {
                        PullParser.Failed(0, expectation)
                    } else {
                        TODO()
                    }
                }

                is PullParser.Matched -> result
                is PullParser.RequireMore -> {
                    if (result.advance == 0) {
                        PullParser.RequireMore(0, EndPullParser(result.parser, optionExpectation))
                    } else {
                        TODO()
                    }
                }
            }
        }
    }
}