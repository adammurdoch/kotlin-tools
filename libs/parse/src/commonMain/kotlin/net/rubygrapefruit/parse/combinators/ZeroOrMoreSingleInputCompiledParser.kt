package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class ZeroOrMoreSingleInputCompiledParser<IN : Input<*>, OUT>(
    val parser: SingleInputParser<IN>,
    val accumulator: RangeAccumulator<IN, OUT>
) : CompiledParser<IN, OUT> {
    override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
        return ZeroOrMorePullParser(parser, accumulator, next)
    }

    private class ZeroOrMorePullParser<IN : Input<*>, OUT>(
        val parser: SingleInputParser<IN>,
        private var accumulator: RangeAccumulator<IN, OUT>,
        val next: ParseContinuation<IN, OUT>
    ) : PullParser<IN> {
        private var matched = 0

        override fun toString(): String {
            return "{zero-or-more-of-one $parser}"
        }

        override fun stop(input: IN): PullParser.Failed {
            return PullParser.Failed(
                listOf(PullParser.Failure(0, parser.expectation)) +
                        next.matched(input, -matched, 0, accumulator).stop(input).failures
            )
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
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
                next.matched(input, index, matched, accumulator, parser.expectation)
            } else {
                PullParser.RequireMore(index, this)
            }
        }
    }
}