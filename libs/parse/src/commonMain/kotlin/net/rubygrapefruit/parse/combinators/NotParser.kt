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
        override fun <NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return NotPullParser(parser, next)
        }
    }

    private class NotPullParser<IN, NEXT>(
        private val parser: CompiledParser<IN, Unit>,
        private val continuation: ParseContinuation<IN, Unit, NEXT>
    ) : PullParser<IN, NEXT> {
        private var predicate = parser.start()
        private var next = continuation.next(0, Unit)
        private var matched = 0

        override fun toString(): String {
            return "{not predicate=$predicate $next}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed.merged(listOf(predicate.stop().map { Expectation.Not(it) }, next.stop()))
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val maxAdvance = min(max, 1)
            val checkResult = predicate.parseZeroOrOne(input, maxAdvance)
            when (checkResult) {
                is PullParser.Matched -> {
                    val predicateExpectation = parser.start().stop().map { Expectation.Not(it) }
                    val nextExpectation = continuation.next(0, Unit).stop()
                    val failure = PullParser.Failed.merged(listOf(predicateExpectation, nextExpectation))
                    return PullParser.Failed(failure.index - matched, failure.expected)
                }

                is PullParser.Failed -> return PullParser.RequireMore(0, next, predicate.stop().expected.map { Expectation.Not(it) })
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