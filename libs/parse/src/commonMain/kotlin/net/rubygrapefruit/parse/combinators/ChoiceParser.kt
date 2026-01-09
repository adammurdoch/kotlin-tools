package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.max

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return ChoicePullParser(choices, converter, next)
    }

    private class ChoicePullParser<IN : Input<*>, OUT, NEXT>(
        parsers: List<Parser<*, OUT>>,
        converter: CombinatorBuilder.Converter<IN>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private var first = 0
        private val matched = BooleanArray(parsers.size)
        private val states = Array<ParseState<IN, NEXT>>(parsers.size) { index ->
            val parser = parsers[index]
            converter.convert(parser) { match ->
                matched[index] = true
                next.matched(match)
            }
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val maxAdvance = max(max, 0)
            for (index in states.indices) {
                val choice = states[index]
                if (choice is PullParser) {
                    val nextChoice = choice.parse(input, maxAdvance)
                    if (matched[index] && index == first) {
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
                } else if (choice is PullParser.Failed) {
                    states[index] = PullParser.Failed(choice.index - maxAdvance, choice.expected)
                }
            }
            return if (requireMore) {
                PullParser.RequireMore(maxAdvance, this)
            } else {
                val failures = states.filterIsInstance<PullParser.Failed<IN, NEXT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
            }
        }
    }
}