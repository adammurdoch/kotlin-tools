package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, ParserBuilder<OUT> {
    override fun <IN : Input<*>> build(converter: ParserBuilder.Converter<IN, OUT>): PullParser<IN, OUT> {
        return ChoicePullParser(choices.map { converter.convert(it) })
    }

    private class ChoicePullParser<IN : Input<*>, OUT>(private val choices: List<PullParser<IN, OUT>>) : PullParser<IN, OUT> {
        override fun parse(input: IN): PullParser.Result<IN, OUT> {
            var requireMore = false
            val results = mutableListOf<PullParser.Result<IN, OUT>>()
            for (choice in choices) {
                val result = choice.parse(input)
                when (result) {
                    is PullParser.Matched -> {
                        return result
                    }

                    is PullParser.Failed -> results.add(result)
                    is PullParser.RequireMore -> {
                        requireMore = true
                        results.add(result)
                    }
                }
            }
            return if (requireMore) {
                val remaining = results.filterIsInstance<PullParser.RequireMore<IN, OUT>>()
                PullParser.RequireMore(ChoicePullParser(remaining.map { it.parser }))
            } else {
                val failures = results.filterIsInstance<PullParser.Failed<IN, OUT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
            }
        }

        override fun endOfInput(input: IN): PullParser.Finished<IN, OUT> {
            val failures = mutableListOf<PullParser.Failed<IN, OUT>>()
            for (choice in choices) {
                val result = choice.endOfInput(input)
                when (result) {
                    is PullParser.Matched -> return result
                    is PullParser.Failed -> failures.add(result)
                }
            }
            val largestIndex = failures.maxOf { it.index }
            val relevantFailures = failures.filter { it.index == largestIndex }
            return PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
        }
    }
}