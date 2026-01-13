package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
        val result = mutableListOf<OUT>()
        val empty = converter.compile(SucceedParser<IN, List<OUT>>(result))
        val option = converter.compile(parser)
        val nested = nested(option, empty, result)
        return ChoiceParser.of(listOf(nested, empty), next)
    }

    private fun <IN : Input<*>> nested(option: CompiledParser<IN, OUT>, empty: CompiledParser<IN, List<OUT>>, result: MutableList<OUT>): CompiledParser<IN, List<OUT>> {
        return object : CompiledParser<IN, List<OUT>> {
            override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
                return option.start(ParseContinuation.of { matched ->
                    result.add(matched.value)
                    val nested = this
                    val parser = ChoiceParser.of(listOf(nested, empty), next)
                    PullParser.RequireMore(matched.count, parser)
                })
            }
        }
    }
}