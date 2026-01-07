package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, ParserBuilder<OUT> {
    override fun <IN : Input<*>> build(converter: ParserBuilder.Converter<IN, OUT>): PullParser<IN, OUT> {
        return ChoicePullParser(choices.map { PullParser.RequireMore(converter.convert(it)) })
    }

    private class ChoicePullParser<IN : Input<*>, OUT>(private val choices: List<PullParser.Result<IN, OUT>>) : PullParser<IN, OUT> {
        override fun parse(input: IN): PullParser.Result<IN, OUT> {
            var requireMore = false
            val nextChoices = mutableListOf<PullParser.Result<IN, OUT>>()
            for (choice in choices) {
                if (choice is PullParser.RequireMore) {
                    val nextChoice = choice.parser.parse(input)
                    when (nextChoice) {
                        is PullParser.Matched -> {
                            return nextChoice
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
                PullParser.RequireMore(ChoicePullParser(nextChoices))
            } else {
                val failures = nextChoices.filterIsInstance<PullParser.Failed<IN, OUT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
            }
        }

        override fun endOfInput(input: IN): PullParser.Finished<IN, OUT> {
            val failures = mutableListOf<PullParser.Failed<IN, OUT>>()
            for (choice in choices) {
                if (choice is PullParser.RequireMore) {
                    val result = choice.parser.endOfInput(input)
                    when (result) {
                        is PullParser.Matched -> return result
                        is PullParser.Failed -> failures.add(result)
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