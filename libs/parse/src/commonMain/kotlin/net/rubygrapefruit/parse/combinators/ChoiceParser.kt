package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class ChoiceParser<IN, OUT>(
    private val options: List<Parser<IN, OUT>>
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return ChoiceParser(options.map { DiscardParser(it) })
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return of(options.map { compiler.compile(it) })
    }

    companion object {
        fun <IN, OUT> of(options: List<CompiledParser<IN, OUT>>): CompiledParser<IN, OUT> {
            val effective = mutableListOf<CompiledParser<IN, OUT>>()
            for (option in options) {
                if (option is ChoiceCompiledParser) {
                    effective.addAll(option.parsers)
                } else {
                    effective.add(option)
                }
            }
            return ChoiceCompiledParser(effective)
        }

        fun <IN, OUT, NEXT> of(parsers: List<CompiledParser<IN, OUT>>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    internal class ChoiceCompiledParser<IN, OUT>(val parsers: List<CompiledParser<IN, OUT>>) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    private class ChoicePullParser<IN, OUT, NEXT>(
        parsers: List<CompiledParser<IN, OUT>>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private val matched = BooleanArray(parsers.size)
        private var currentExpected: Expectation? = null
        private val states = Array<ParseState<IN, NEXT>>(parsers.size) { index ->
            val parser = parsers[index]
            parser.start { length, value ->
                matched[index] = true
                next.next(length, value)
            }
        }

        override val expectation: Expectation
            get() {
                val currentExpected = currentExpected
                return if (currentExpected == null) {
                    val expected = Expectation.OneOf.of(states.mapNotNull {
                        if (it is PullParser) {
                            it.expectation
                        } else if (it is PullParser.Failed && it.index == 0) {
                            it.expected
                        } else {
                            null
                        }
                    })
                    this.currentExpected = expected
                    expected
                } else {
                    currentExpected
                }
            }

        override fun toString(): String {
            return "{choice}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val maxAdvance = min(max, 1)
            for (index in states.indices) {
                val choice = states[index]
                if (choice is PullParser) {
                    val nextChoice = choice.parseZeroOrOne(input, maxAdvance)
                    if (matched[index] && !requireMore) {
                        // Could fail at the same location as other choices
                        if (nextChoice is PullParser.Failed) {
                            states[index] = nextChoice
                            return mergedFailures()
                        }
                        return nextChoice
                    }
                    when (nextChoice) {
                        is PullParser.Finished -> {
                            states[index] = nextChoice
                        }

                        is PullParser.RequireMore -> {
                            requireMore = true
                            states[index] = nextChoice.parser
                        }
                    }
                }
            }
            return if (requireMore) {
                if (maxAdvance > 0) {
                    for (index in states.indices) {
                        val choice = states[index]
                        if (choice is PullParser.Failed) {
                            states[index] = PullParser.Failed(choice.index - maxAdvance, choice.expected)
                        }
                    }
                    currentExpected = null
                }
                PullParser.RequireMore(maxAdvance, this)
            } else {
                mergedFailures()
            }
        }

        private fun mergedFailures(): PullParser.Failed {
            val failures = states.filterIsInstance<PullParser.Failed>()
            val largestIndex = failures.maxOf { it.index }
            val relevantFailures = failures.filter { it.index == largestIndex }
            return PullParser.Failed(largestIndex, Expectation.OneOf.of(relevantFailures.map { it.expected }))
        }
    }
}