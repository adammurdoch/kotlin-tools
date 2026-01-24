package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class NotParser<IN>(private val parser: Parser<IN, *>) : Parser<IN, Unit>, CombinatorBuilder<Unit> {
    override fun withNoResult(): CombinatorBuilder<Unit> {
        TODO()
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return NotCompiledParser(compiler.compile(parser))
    }

    internal class NotCompiledParser<IN>(
        val parser: CompiledParser<IN, *>
    ) : CompiledParser<IN, Unit> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return NotPullParser(parser.start(), next.next(0, Unit))
        }
    }

    private class NotPullParser<IN, NEXT>(
        private var predicate: PullParser<IN, *>,
        private var next: PullParser<IN, NEXT>
    ) : PullParser<IN, NEXT> {
        private var matched = 0
        private val expectedAtStart = Expectation.OneOf.of(Expectation.Not(predicate.expectation), next.expectation)

        override val expectation: Expectation
            get() = next.expectation

        override fun toString(): String {
            return "{not predicate=$predicate $next}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val maxAdvance = min(max, 1)
            val checkResult = predicate.parseZeroOrOne(input, maxAdvance)
            when (checkResult) {
                is PullParser.Matched -> return PullParser.Failed(-matched, expectedAtStart)
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