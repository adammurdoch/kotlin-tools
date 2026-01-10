package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
        val result = mutableListOf<OUT>()
        val empty = SucceedParser<IN, List<OUT>>(result)
        val nested = nested(converter, empty, result)
        return ChoiceParser.of(listOf(nested, empty), next)
    }

    private fun <IN : Input<*>> nested(converter: CombinatorBuilder.Converter<IN>, empty: ParserBuilder<IN, List<OUT>>, result: MutableList<OUT>): ParserBuilder<IN, List<OUT>> {
        return object : ParserBuilder<IN, List<OUT>> {
            override fun <NEXT> build(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
                return converter.convert(parser) { matched ->
                    result.add(matched.value)
                    val nested = nested(converter, empty, result)
                    val parser = ChoiceParser.of(listOf(nested, empty), next)
                    PullParser.RequireMore(matched.count, parser)
                }
            }
        }
    }
}