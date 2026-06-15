package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class RangeSingleInputCompiledParser<IN : Input<*>, OUT>(
    val range: Range,
    val predicate: InputPredicate<IN>,
    val expectation: Expectation,
    val accumulator: RangeAccumulator<IN, OUT>
) : CompiledParser<IN, OUT> {
    override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
        return RepeatingPullParser(range, predicate, expectation, accumulator, next)
    }

    private class RepeatingPullParser<IN : Input<*>, OUT>(
        val range: Range,
        val predicate: InputPredicate<IN>,
        val expectation: Expectation,
        private var accumulator: RangeAccumulator<IN, OUT>,
        val next: ParseContinuation<IN, OUT>
    ) : PullParser<IN> {
        private var matched = 0

        override fun toString(): String {
            return "{${range.diagnostic} $predicate}"
        }

        override fun stop(input: IN): PullParser.Failed {
            val failure = next.failed(0, matched, expectation)
            return if (matched < range.min) {
                failure
            } else {
                PullParser.Failed(failure.failures + next.matched(input, -matched, 0, accumulator).stop(input).failures)
            }
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            var index = 0
            while (index < max) {
                if (!predicate.match(input, index)) {
                    break
                }
                index++
            }
            if (index > 0) {
                matched += index
                accumulator = accumulator.extract(input, 0, index)
            }
            return if (index < max || index == input.available && input.finished) {
                // Found all the matching inputs - either found an input that does not match or the end of the inputs
                if (matched < range.min) {
                    next.failed(index, matched, expectation)
                } else {
                    next.matched(input, index, matched, accumulator, expectation)
                }
            } else {
                PullParser.RequireMore(index, this)
            }
        }
    }
}