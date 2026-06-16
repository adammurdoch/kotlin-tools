package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input
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
        private var next: PullParser<IN>? = null
        private var nextAdvance = 1
        private var totalAdvanced = 0

        override fun toString(): String {
            return "{not predicate=$predicate next=$next}"
        }

        override fun stop(input: IN): PullParser.Failed {
            return PullParser.Failed.Flatten(listOf(predicate.stop(input).map { failure -> failure.map { Expectation.Not(it) } }, next(input).stop(input)))
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            val maxAdvance = min(max, 1)
            if (nextAdvance > 0) {
                val checkResult = predicate.parse(input, maxAdvance)
                when (checkResult) {
                    is PullParser.Matched -> {
                        // Fail at the start
                        val predicateExpectation = predicateExpectationAtStart(input).map { failure ->
                            PullParser.Failure(failure.index - totalAdvanced, failure.expected)
                        }
                        val nextExpectation = continuation.matched(input, 0, 0, ValueProvider.Nothing).stop(input).map { failure ->
                            PullParser.Failure(failure.index - totalAdvanced, failure.expected)
                        }
                        return PullParser.Failed.Flatten(listOf(predicateExpectation, nextExpectation))
                    }

                    is PullParser.Failed -> {
                        val predicateExpectation = predicateExpectationAtStart(input).map { failure ->
                            PullParser.Failure(failure.index - totalAdvanced, failure.expected)
                        }
                        return continuation.selected(0, next(input), predicateExpectation)
                    }

                    is PullParser.RequireMore -> {
                        predicate = checkResult.parser
                        if (checkResult.advance == 0) {
                            return PullParser.RequireMore(0, this)
                        }
                    }
                }
            }

            val next = next(input)
            val result = next.parse(input, maxAdvance)
            when (result) {
                is PullParser.Failed -> {
                    val predicateExpectation = predicateExpectationAtStart(input).map { failure ->
                        PullParser.Failure(failure.index - totalAdvanced, failure.expected)
                    }
                    return PullParser.Failed.Flatten(listOf(predicateExpectation, result))
                }

                is PullParser.Matched -> {
                    this.next = result.parser
                    nextAdvance = result.advance
                }

                is PullParser.RequireMore -> {
                    this.next = result.parser
                    nextAdvance = result.advance
                }
            }
            totalAdvanced += nextAdvance
            return PullParser.RequireMore(nextAdvance, this)
        }

        private fun predicateExpectationAtStart(input: IN): PullParser.Failed =
            parser.start(start, ParseContinuation.end()).stop(input).map { failure -> failure.map { Expectation.Not(it) } }

        private fun next(input: IN): PullParser<IN> {
            if (next == null) {
                next = continuation.matched(input, 0, 0, ValueProvider.Nothing).parser()
            }
            return this.next!!
        }
    }
}