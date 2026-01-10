package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return ChoicePullParser(choices.map { converter.convert(it) }, next)
    }

    companion object {
        fun <IN : Input<*>, OUT, NEXT> of(parsers: List<PullParser<IN, OUT>>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    private class ChoicePullParser<IN : Input<*>, OUT, NEXT>(
        parsers: List<PullParser<IN, OUT>>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private var first = 0
        private val states = Array<Choice<IN, OUT, NEXT>>(parsers.size) { index ->
            val parser = parsers[index]
            Matching(parser)
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val maxAdvance = min(max, 1)
            for (index in states.indices) {
                val choice = states[index]
                when (choice) {
                    is Matching -> {
                        val nextChoice = parse(choice.parser, input, maxAdvance)
                        when (nextChoice) {
                            is PullParser.Matched -> {
                                val result = next.matched(nextChoice)
                                if (index == first) {
                                    return result
                                }
                                when (result) {
                                    is PullParser.Matched -> {
                                        states[index] = Finished()
                                    }

                                    is PullParser.Failed -> {
                                        states[index] = Failed(result.index, result.expected)
                                    }

                                    is PullParser.RequireMore -> {
                                        states[index] = Continuing(result.parser)
                                    }
                                }
                            }

                            is PullParser.Failed -> {
                                if (index == first) {
                                    first++
                                }
                                states[index] = Failed(nextChoice.index, nextChoice.expected)
                            }

                            is PullParser.RequireMore -> {
                                requireMore = true
                                choice.parser = nextChoice.parser
                            }
                        }
                    }

                    is Continuing -> {
                        val nextChoice = parse(choice.parser, input, maxAdvance)
                        when (nextChoice) {
                            is PullParser.Matched -> {
                                if (index == first) {
                                    return nextChoice
                                }
                                states[index] = Finished()
                            }

                            is PullParser.Failed -> states[index] = Failed(nextChoice.index, nextChoice.expected)
                            is PullParser.RequireMore -> {
                                requireMore = true
                                choice.parser = nextChoice.parser
                            }
                        }
                    }

                    is Failed, is Finished -> {}
                }
            }
            return if (requireMore) {
                if (maxAdvance > 0) {
                    for (index in states.indices) {
                        val choice = states[index]
                        if (choice is Failed) {
                            choice.index -= maxAdvance
                        }
                    }
                }
                PullParser.RequireMore(maxAdvance, this)
            } else {
                val failures = states.filterIsInstance<Failed<IN, OUT, NEXT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
            }
        }

        private fun <OUT> parse(parser: PullParser<IN, OUT>, input: IN, maxAdvance: Int): PullParser.Result<IN, OUT> {
            var current = parser
            while (true) {
                val result = current.parse(input, maxAdvance)
                if (maxAdvance == 1 && result is PullParser.RequireMore && result.advance == 0) {
                    current = result.parser
                    continue
                }
                return result
            }
        }
    }

    private sealed class Choice<IN, OUT, NEXT>

    private class Matching<IN, OUT, NEXT>(var parser: PullParser<IN, OUT>) : Choice<IN, OUT, NEXT>()

    private class Continuing<IN, OUT, NEXT>(var parser: PullParser<IN, NEXT>) : Choice<IN, OUT, NEXT>()

    private class Failed<IN, OUT, NEXT>(var index: Int, val expected: List<String>) : Choice<IN, OUT, NEXT>()

    private class Finished<IN, OUT, NEXT> : Choice<IN, OUT, NEXT>()
}