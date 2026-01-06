package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, ParserBuilder<OUT> {
    override fun <IN: Input<*>> build(converter: ParserBuilder.Converter<IN, OUT>): PullParser<IN, OUT> {
        return ChoicePullParser(choices.map { converter.convert(it) })
    }

    private class ChoicePullParser<IN: Input<*>, OUT>(private val choices: List<PullParser<IN, OUT>>) : PullParser<IN, OUT> {
        override fun parse(input: IN): PullParser.Result<IN, OUT> {
            var requireMore = false
            val expected = mutableListOf<String>()
            for (choice in choices) {
                val result = choice.parse(input)
                when (result) {
                    is PullParser.Matched -> {
                        return result
                    }

                    is PullParser.Failed -> expected.addAll(result.expected)
                    is PullParser.RequireMore -> requireMore = true
                }
            }
            return if (requireMore) {
                PullParser.RequireMore(this)
            } else {
                PullParser.Failed(0, expected)
            }
        }

        override fun endOfInput(input: IN): PullParser.Finished<IN, OUT> {
            val expected = mutableListOf<String>()
            for (choice in choices) {
                val result = choice.endOfInput(input)
                when (result) {
                    is PullParser.Matched -> return result
                    is PullParser.Failed -> expected.addAll(result.expected)
                }
            }
            return PullParser.Failed(0, expected)
        }
    }
}