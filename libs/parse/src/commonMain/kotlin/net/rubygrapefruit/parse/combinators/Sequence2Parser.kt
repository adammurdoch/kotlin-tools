package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class Sequence2Parser<IN, A, B, OUT>(
    private val a: Parser<IN, A>,
    private val b: Parser<IN, B>,
    private val map: (A, B) -> OUT
) : Parser<IN, OUT>, ParserBuilder<OUT> {
    override fun <IN : Input<*>> build(converter: ParserBuilder.Converter<IN>): PullParser<IN, OUT> {
        return SequencePullParser(converter.convert(a)) { resultA ->
            PullParser.RequireMore(SequencePullParser(converter.convert(b)) { resultB ->
                PullParser.Matched(resultA.count + resultB.count, map(resultA.value, resultB.value))
            })
        }
    }

    private class SequencePullParser<IN, T, OUT>(
        private val parser: PullParser<IN, T>,
        private val next: (PullParser.Matched<IN, T>) -> PullParser.Result<IN, OUT>
    ) : PullParser<IN, OUT> {
        override fun parse(input: IN): PullParser.Result<IN, OUT> {
            val result = parser.parse(input)
            return when (result) {
                is PullParser.Matched -> next(result)
                is PullParser.Failed -> TODO()
                is PullParser.RequireMore -> PullParser.RequireMore(this)
            }
        }

        override fun endOfInput(input: IN): PullParser.Finished<IN, OUT> {
            val result = parser.endOfInput(input)
            TODO()
        }
    }
}