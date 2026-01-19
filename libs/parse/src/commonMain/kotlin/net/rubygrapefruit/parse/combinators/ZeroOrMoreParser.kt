package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        val option = compiler.compile(parser)
        return ZeroOrMoreCompiledParser(option, Empty())
    }

    companion object {
        fun <IN, ITEM, OUT> of(option: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return ZeroOrMoreCompiledParser(option, initial)
        }
    }

    private class ZeroOrMoreCompiledParser<IN, ITEM, OUT>(
        val option: CompiledParser<IN, ITEM>,
        initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        private val empty = EmptyCompiledParser<IN, ITEM, OUT>(initial)
        private val nested = OptionCompiledParser(option, initial)

        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = option.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoiceParser.of(listOf(nested, empty), next)
        }
    }

    private class OptionCompiledParser<IN, ITEM, OUT>(
        val option: CompiledParser<IN, ITEM>,
        val previous: Accumulator<ITEM, OUT>
    ) :
        CompiledParser<IN, OUT> {

        override val mayNotAdvanceOnMatch: Boolean
            get() = option.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = option.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return option.start { matched ->
                val length = matched.end - matched.start
                val result = previous.add(matched.value, length)
                val parser = if (length == 0) {
                    SucceedParser.start(result.value, next)
                } else {
                    val empty = EmptyCompiledParser<IN, ITEM, OUT>(result)
                    val nested = OptionCompiledParser(option, result)
                    ChoiceParser.of(listOf(nested, empty), next)
                }
                PullParser.RequireMore(matched.end, parser)
            }
        }
    }

    private class EmptyCompiledParser<IN, ITEM, OUT>(val result: Accumulator<ITEM, OUT>) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return SucceedParser.start(result.value, next, length = result.length)
        }
    }
}