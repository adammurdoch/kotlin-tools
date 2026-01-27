package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class NotParser<IN>(private val parser: Parser<IN, Unit>) : Parser<IN, Unit>, CombinatorBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return NotCompiledParser(compiler.compile(parser))
    }

    internal class NotCompiledParser<IN>(
        val parser: CompiledParser<IN, Unit>
    ) : CompiledParser<IN, Unit> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Not(parser.expectation)

        override fun <NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return NotPullParser(parser, next.next(0, Unit))
        }
    }

    private class NotPullParser<IN, NEXT>(
        private val parser: CompiledParser<IN, Unit>,
        private var next: PullParser<IN, NEXT>
    ) : PullParser<IN, NEXT> {
        private var predicate = parser.start()
        private var matched = 0

        override val expectation: Expectation
            get() = Expectation.OneOf.of(Expectation.Not(predicate.expectation), next.expectation)

        override fun toString(): String {
            return "{not predicate=$predicate $next}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val maxAdvance = min(max, 1)
            val checkResult = predicate.parseZeroOrOne(input, maxAdvance)
            when (checkResult) {
                is PullParser.Matched -> return PullParser.Failed(-matched, Expectation.OneOf.of(Expectation.Not(parser.start().expectation), next.expectation))
                is PullParser.Failed -> return PullParser.RequireMore(0, MergeExpectationsPullParser(next, Expectation.Not(predicate.expectation)))
                is PullParser.RequireMore -> predicate = checkResult.parser
            }

            val result = next.parseZeroOrOne(input, maxAdvance)
            when (result) {
                is PullParser.Finished -> return result
                is PullParser.RequireMore -> next = result.parser
            }
            matched += maxAdvance
            return PullParser.RequireMore(maxAdvance, this)
        }
    }
}