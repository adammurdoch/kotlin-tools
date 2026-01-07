package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.PullParser

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT> {
        return ChoicePullParser(
            choices.map { parser -> PullParser.RequireMore(converter.convert(parser) { matched -> matched }) },
            next
        )
    }

    private class ChoicePullParser<IN : Input<*>, OUT, NEXT>(
        private val choices: List<PullParser.Result<IN, OUT>>,
        private val next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>
    ) : PullParser<IN, NEXT> {
        override fun parse(input: IN): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val nextChoices = mutableListOf<PullParser.Result<IN, OUT>>()
            for (choice in choices) {
                if (choice is PullParser.RequireMore) {
                    val nextChoice = choice.parser.parse(input)
                    when (nextChoice) {
                        is PullParser.Matched -> {
                            return next(nextChoice)
                        }

                        is PullParser.Failed -> nextChoices.add(nextChoice)
                        is PullParser.RequireMore -> {
                            requireMore = true
                            nextChoices.add(nextChoice)
                        }
                    }
                } else {
                    nextChoices.add(choice)
                }
            }
            return if (requireMore) {
                PullParser.RequireMore(ChoicePullParser(nextChoices, next))
            } else {
                val failures = nextChoices.filterIsInstance<PullParser.Failed<IN, OUT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
            }
        }

        override fun endOfInput(input: IN): PullParser.Finished<IN, NEXT> {
            val failures = mutableListOf<PullParser.Failed<IN, OUT>>()
            for (choice in choices) {
                if (choice is PullParser.RequireMore) {
                    val nextChoice = choice.parser.endOfInput(input)
                    when (nextChoice) {
                        is PullParser.Matched -> {
                            return next(nextChoice) as PullParser.Finished<IN, NEXT>
                        }
                        is PullParser.Failed -> failures.add(nextChoice)
                    }
                } else {
                    failures.add(choice as PullParser.Failed)
                }
            }
            val largestIndex = failures.maxOf { it.index }
            val relevantFailures = failures.filter { it.index == largestIndex }
            return PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
        }
    }
}