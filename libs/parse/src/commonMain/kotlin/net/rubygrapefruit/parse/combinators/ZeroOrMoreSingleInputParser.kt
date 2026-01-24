package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreSingleInputParser<IN : BoxingInput<*, OUT>, OUT>(private val parser: SingleInputParser<IN>) : CompiledParser<IN, List<OUT>> {
    override val mayNotAdvanceOnMatch: Boolean
        get() = true

    override val expectation: Expectation
        get() = parser.expectation

    override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
        return ZeroOrMorePullParser(parser, next)
    }

    private class ZeroOrMorePullParser<IN : BoxingInput<*, OUT>, OUT, NEXT>(
        val parser: SingleInputParser<IN>,
        val next: ParseContinuation<IN, List<OUT>, NEXT>
    ) : PullParser<IN, NEXT> {
        private val result = mutableListOf<OUT>()

        override val expectation: Expectation
            get() = Expectation.OneOf.of(parser.expectation, next.expectation)

        override fun toString(): String {
            return "{zero-or-more $parser}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var index = 0
            while (index < max) {
                if (!parser.match(input, index)) {
                    break
                }
                result.add(input.getBoxed(index))
                index++
            }
            return if (index < max || index == input.available && input.finished) {
                val nextParser = MergeExpectationsPullParser(next.next(result.size, result), expectation)
                PullParser.RequireMore(index, nextParser)
            } else {
                PullParser.RequireMore(index, this)
            }
        }
    }
}