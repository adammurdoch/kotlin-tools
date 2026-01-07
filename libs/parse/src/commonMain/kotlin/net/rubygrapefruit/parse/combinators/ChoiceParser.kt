package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ChoiceParser<IN, OUT>(private val choices: List<Parser<IN, OUT>>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT> {
        return ChoicePullParser(
            choices.map { parser -> converter.convert(parser) { matched -> matched } },
            next
        )
    }

    private class ChoicePullParser<IN : Input<*>, OUT, NEXT>(
        private val choices: List<ParseState<IN, OUT>>,
        private val next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>
    ) : PullParser<IN, NEXT> {
        override fun parse(input: IN): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val nextChoices = mutableListOf<ParseState<IN, OUT>>()
            for (choice in choices) {
                if (choice is PullParser) {
                    val nextChoice = choice.parse(input)
                    when (nextChoice) {
                        is PullParser.Matched -> {
                            return next(nextChoice)
                        }

                        is PullParser.Failed -> nextChoices.add(nextChoice)
                        is PullParser.RequireMore -> {
                            requireMore = true
                            nextChoices.add(nextChoice.parser)
                        }
                    }
                } else {
                    nextChoices.add(choice)
                }
            }
            return if (requireMore) {
                PullParser.RequireMore(0, ChoicePullParser(nextChoices, next))
            } else {
                val failures = nextChoices.filterIsInstance<PullParser.Failed<IN, OUT>>()
                val largestIndex = failures.maxOf { it.index }
                val relevantFailures = failures.filter { it.index == largestIndex }
                PullParser.Failed(largestIndex, relevantFailures.flatMap { it.expected })
            }
        }
    }
}