package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
        val result = mutableListOf<OUT>()
        val succeedParser = SucceedParser<List<OUT>>(result)
        val empty = converter.convert(succeedParser)
        val nested = nested(converter, empty, result)
        return ChoiceParser.of(listOf(nested, empty), next)
    }

    private fun <IN : Input<*>> nested(converter: CombinatorBuilder.Converter<IN>, empty: PullParser<IN, List<OUT>>, result: MutableList<OUT>): PullParser<IN, List<OUT>> {
        return converter.convert(parser) { matched ->
            result.add(matched.value)
            val nested = nested(converter, empty, result)
            val parser = ChoiceParser.of(listOf(nested, empty), ParseContinuation.of())
            PullParser.RequireMore(matched.count, parser)
        }
    }
}