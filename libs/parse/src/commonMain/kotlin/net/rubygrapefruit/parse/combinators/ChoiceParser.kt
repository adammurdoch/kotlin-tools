package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(converter: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return ChoiceCompiledParser(choices.map { converter.compile(it) })
    }

    companion object {
        fun <IN, OUT, NEXT> of(parsers: List<CompiledParser<IN, OUT>>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    private class ChoiceCompiledParser<IN, OUT>(private val parsers: List<CompiledParser<IN, OUT>>) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean = parsers.any { it.mayNotAdvanceOnMatch }

        override val expectation: Expectation = Expectation.OneOf(parsers.map { it.expectation })

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    private class ChoicePullParser<IN, OUT, NEXT>(
        parsers: List<CompiledParser<IN, OUT>>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private var first = 0
        private val matched = BooleanArray(parsers.size)
        private var currentExpected: Expectation? = null
        private val states = Array<ParseState<IN, NEXT>>(parsers.size) { index ->
            val parser = parsers[index]
            parser.start { match ->
                matched[index] = true
                next.matched(match)
            }
        }

        override val expected: Expectation
            get() {
                val currentExpected = currentExpected
                return if (currentExpected == null) {
                    val expected = Expectation.OneOf(states.mapNotNull { if (it is PullParser) it.expected else null })
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
                    if (matched[index] && index == first && nextChoice !is PullParser.Failed) {
                        // Could fail at the same location as other choices
                        return nextChoice
                    }
                    when (nextChoice) {
                        is PullParser.Finished -> {
                            states[index] = nextChoice
                            if (index == first) {
                                first++
                            }
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
                val failures = states.filterIsInstance<PullParser.Failed<IN, NEXT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, Expectation.OneOf(relevantFailures.map { it.expected }))
            }
        }
    }
}