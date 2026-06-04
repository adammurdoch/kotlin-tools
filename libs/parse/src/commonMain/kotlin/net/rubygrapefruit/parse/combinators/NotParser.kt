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
        override fun start(start: Position, next: ParseContinuation<IN, Unit>): PullParser<IN> {
            return NotPullParser(parser, start, next)
        }
    }

    private class NotPullParser<IN>(
        private val parser: CompiledParser<IN, Unit>,
        private val start: Position,
        private val continuation: ParseContinuation<IN, Unit>
    ) : PullParser<IN> {
        private var predicate = parser.start(start, ParseContinuation.end())
        private var next = continuation.matched(0, 0, ValueProvider.Nothing).parser
        private var nextAdvance = 1
        private var nextCommit = 0
        private var totalAdvanced = 0

        override fun toString(): String {
            return "{not predicate=$predicate next=$next}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed.merged(listOf(predicate.stop().map { Expectation.Not(it) }, next.stop()))
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            val maxAdvance = min(max, 1)
            if (nextAdvance > 0) {
                val checkResult = predicate.parse(input, maxAdvance)
                when (checkResult) {
                    is PullParser.Matched -> {
                        val predicateExpectation = parser.start(start, ParseContinuation.end()).stop().map { Expectation.Not(it) }
                        val nextExpectation = continuation.matched(0, 0, ValueProvider.Nothing).parser.stop()
                        val failure = PullParser.Failed.merged(listOf(predicateExpectation, nextExpectation))
                        return PullParser.Failed(failure.index - totalAdvanced, failure.expected)
                    }

                    is PullParser.Failed -> return continuation.selected(0, nextCommit, next, predicate.stop().expected.map { Expectation.Not(it) })
                    is PullParser.RequireMore -> {
                        predicate = checkResult.parser
                        if (checkResult.advance == 0) {
                            return PullParser.RequireMore(0, 0, false, this, null)
                        }
                    }
                }
            }

            val result = next.parse(input, maxAdvance)
            when (result) {
                is PullParser.Matched -> return result
                is PullParser.Failed -> {
                    return if (result.index == -totalAdvanced) {
                        val predicateExpectation = parser.start(start, ParseContinuation.end()).stop().expected.map { Expectation.Not(it) }
                        PullParser.Failed(result.index, ExpectationProvider.oneOf(predicateExpectation, result.expected))
                    } else {
                        result
                    }
                }

                is PullParser.RequireMore -> {
                    next = result.parser
                    nextAdvance = result.advance
                    nextCommit += result.commit
                }
            }
            totalAdvanced += nextAdvance
            return PullParser.RequireMore(nextAdvance, 0, false, this)
        }
    }
}