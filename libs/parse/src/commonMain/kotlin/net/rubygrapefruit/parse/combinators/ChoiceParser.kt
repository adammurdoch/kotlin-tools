package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return ChoicePullParser(choices.map { converter.builder(it) }, next)
    }

    companion object {
        fun <IN : Input<*>, OUT, NEXT> of(parsers: List<ParserBuilder<IN, OUT>>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    private class ChoicePullParser<IN : Input<*>, OUT, NEXT>(
        parsers: List<ParserBuilder<IN, OUT>>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private var first = 0
        private val matched = BooleanArray(parsers.size)
        private val states = Array<ParseState<IN, NEXT>>(parsers.size) { index ->
            val parser = parsers[index]
            parser.build(ParseContinuation.of { match ->
                matched[index] = true
                next.matched(match)
            })
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val maxAdvance = min(max, 1)
            for (index in states.indices) {
                val choice = states[index]
                if (choice is PullParser) {
                    val nextChoice = parse(choice, input, maxAdvance)
                    if (matched[index] && index == first && nextChoice !is PullParser.Failed) {
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
                }
                PullParser.RequireMore(maxAdvance, this)
            } else {
                val failures = states.filterIsInstance<PullParser.Failed<IN, NEXT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected }.distinct().sorted())
            }
        }

        private fun parse(parser: PullParser<IN, NEXT>, input: IN, maxAdvance: Int): PullParser.Result<IN, NEXT> {
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
}