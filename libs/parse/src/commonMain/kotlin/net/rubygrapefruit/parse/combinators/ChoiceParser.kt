package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return ChoicePullParser(choices, converter, next)
    }

    private class ChoicePullParser<IN : Input<*>, OUT, NEXT>(
        parsers: List<Parser<*, OUT>>,
        converter: CombinatorBuilder.Converter<IN>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private var firstFinished = parsers.size
        private val states: MutableList<ParseState<IN, NEXT>> = parsers.mapIndexed { index, parser ->
            converter.convert(parser) { matched ->
                firstFinished = min(index, firstFinished)
                next.matched(matched)
            }
        }.toMutableList()

        override fun parse(input: IN): PullParser.Result<IN, NEXT> {
            var requireMore = false
            for (index in states.indices) {
                val choice = states[index]
                if (choice is PullParser) {
                    val nextChoice = choice.parse(input)
                    if (firstFinished == index) {
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
                PullParser.RequireMore(0, this)
            } else {
                val failures = states.filterIsInstance<PullParser.Failed<IN, NEXT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
            }
        }
    }
}