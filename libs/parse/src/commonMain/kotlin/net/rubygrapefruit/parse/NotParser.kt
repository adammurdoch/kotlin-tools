package net.rubygrapefruit.parse

import kotlin.math.min

internal class NotParser<IN>(private val parser: Parser<IN, Unit>) : Parser<IN, Unit>, CombinatorBuilder<Unit> {
    override fun <IN : Input<*>> compile(converter: CombinatorBuilder.Converter<IN>): CompiledParser<IN, Unit> {
        return NotCompiledParser(converter.compile(parser))
    }

    private class NotCompiledParser<IN>(
        private val parser: CompiledParser<IN, Unit>
    ) : CompiledParser<IN, Unit> {
        override fun <NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return NotPullParser(parser.start(), NextParser(next))
        }
    }

    private class NotPullParser<IN, NEXT>(
        private var predicate: PullParser<IN, Unit>,
        private var next: PullParser<IN, NEXT>
    ) : PullParser<IN, NEXT> {
        private var matched = 0

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val maxAdvance = min(max, 1)
            val checkResult = predicate.parseZeroOrOne(input, maxAdvance)
            when (checkResult) {
                is PullParser.Matched -> return PullParser.Failed(-matched, Expectation.Nothing) // next could fail at the same location
                is PullParser.Failed -> return PullParser.RequireMore(0, next)
                is PullParser.RequireMore -> predicate = checkResult.parser
            }

            val result = next.parseZeroOrOne(input, maxAdvance)
            when (result) {
                is PullParser.Matched -> return result
                is PullParser.Failed -> return result
                is PullParser.RequireMore -> next = result.parser
            }
            matched += maxAdvance
            return PullParser.RequireMore(maxAdvance, this)
        }
    }

    private class NextParser<IN, NEXT>(private val next: ParseContinuation<IN, Unit, NEXT>) : PullParser<IN, NEXT> {
        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return next.matched(0, Unit)
        }
    }
}