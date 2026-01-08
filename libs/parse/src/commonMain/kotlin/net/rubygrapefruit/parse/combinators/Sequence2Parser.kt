package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class Sequence2Parser<IN, A, B, OUT>(
    private val a: Parser<IN, A>,
    private val b: Parser<IN, B>,
    private val map: (A, B) -> OUT
) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return converter.convert(a) { resultA ->
            val parserB = converter.convert(b) { resultB ->
                val result = map(resultA.value, resultB.value)
                next.matched(resultB.count, result)
            }
            PullParser.RequireMore(resultA.count, parserB)
        }
    }
}