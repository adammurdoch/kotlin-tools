package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.PullParser

internal class MergeExpectationsPullParser<IN, OUT>(val parser: PullParser<IN, OUT>, val optionExpectation: Expectation) : PullParser<IN, OUT> {
    override fun toString(): String {
        return "{merge-expectations $parser}"
    }

    override fun stop(): PullParser.Failed {
        val failure = parser.stop()
        return PullParser.Failed.merged(listOf(PullParser.Failed(0, optionExpectation), failure))
    }

    override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
        val result = parser.parse(input, max)
        return when (result) {
            is PullParser.Failed -> {
                PullParser.Failed.merged(listOf(PullParser.Failed(0, optionExpectation), result))
            }

            is PullParser.Matched -> result
            is PullParser.RequireMore -> {
                if (result.advance == 0) {
                    PullParser.RequireMore(0, MergeExpectationsPullParser(result.parser, optionExpectation))
                } else {
                    result
                }
            }
        }
    }
}
