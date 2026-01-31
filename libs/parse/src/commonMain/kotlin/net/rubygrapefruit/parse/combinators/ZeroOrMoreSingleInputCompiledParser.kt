package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreSingleInputCompiledParser<IN : Input<*>, OUT>(
    val parser: SingleInputParser<IN>,
    val accumulator: RangeAccumulator<IN, OUT>
) : CompiledParser<IN, OUT> {
    override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return ZeroOrMorePullParser(parser, accumulator, next)
    }

    private class ZeroOrMorePullParser<IN : Input<*>, OUT, NEXT>(
        val parser: SingleInputParser<IN>,
        private var accumulator: RangeAccumulator<IN, OUT>,
        val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private var matched = 0

        override fun toString(): String {
            return "{zero-or-more-of-one $parser}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed.merged(listOf(PullParser.Failed(0, parser.expectation), next.next(matched, accumulator).stop()))
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var index = 0
            while (index < max) {
                if (!parser.match(input, index)) {
                    break
                }
                index++
            }
            if (index > 0) {
                matched += index
                accumulator = accumulator.extract(input, 0, index)
            }
            return if (index < max || index == input.available && input.finished) {
                val nextParser = next.next(matched, accumulator)
                PullParser.RequireMore(index, nextParser, parser.expectation)
            } else {
                PullParser.RequireMore(index, this)
            }
        }
    }
}