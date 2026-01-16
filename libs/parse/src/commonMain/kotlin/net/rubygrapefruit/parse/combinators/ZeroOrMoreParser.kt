package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        val option = compiler.compile(parser)
        return ZeroOrMoreCompiledParser(option)
    }

    private class ZeroOrMoreCompiledParser<IN, OUT>(
        val option: CompiledParser<IN, OUT>,
    ) : CompiledParser<IN, List<OUT>> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = option.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            val result = Collector<OUT>()
            val empty = EmptyCompiledParser<IN, OUT>(result)
            val nested = OptionCompiledParser(option, empty, result)
            return ChoiceParser.of(listOf(nested, empty), next)
        }
    }

    private class Collector<T> {
        var length: Int = 0
        val items = mutableListOf<T>()
    }

    private class OptionCompiledParser<IN, OUT>(val option: CompiledParser<IN, OUT>, val empty: CompiledParser<IN, List<OUT>>, val result: Collector<OUT>) :
        CompiledParser<IN, List<OUT>> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = option.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = option.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return option.start { matched ->
                result.items.add(matched.value)
                result.length += (matched.end - matched.start)
                val parser = ChoiceParser.of(listOf(this, empty), next)
                PullParser.RequireMore(matched.end, parser)
            }
        }
    }

    private class EmptyCompiledParser<IN, OUT>(val result: Collector<OUT>) : CompiledParser<IN, List<OUT>> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return SucceedParser.start(result.items, next, length = result.length)
        }
    }
}