package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>> compile(converter: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        val option = converter.compile(parser)
        return ZeroOrMoreCompiledParser(option)
    }

    private class ZeroOrMoreCompiledParser<IN, OUT>(
        val option: CompiledParser<IN, OUT>,
    ) : CompiledParser<IN, List<OUT>> {
        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            val result = mutableListOf<OUT>()
            val empty = EndSequenceCompiledParser<IN, OUT>(result)
            val nested = nested(option, empty, result)
            return ChoiceParser.of(listOf(nested, empty), next)
        }

        private fun <IN> nested(option: CompiledParser<IN, OUT>, empty: CompiledParser<IN, List<OUT>>, result: MutableList<OUT>): CompiledParser<IN, List<OUT>> {
            return object : CompiledParser<IN, List<OUT>> {
                override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
                    return option.start { matched ->
                        result.add(matched.value)
                        val parser = ChoiceParser.of(listOf(this, empty), next)
                        PullParser.RequireMore(matched.count, parser)
                    }
                }
            }
        }
    }

    private class EndSequenceCompiledParser<IN, OUT>(val result: List<OUT>) : CompiledParser<IN, List<OUT>> {
        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return object : PullParser<IN, NEXT> {
                override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
                    return next.matched(0, result)
                }
            }
        }
    }
}