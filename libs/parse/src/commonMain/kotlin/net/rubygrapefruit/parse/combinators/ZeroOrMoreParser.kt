package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, TypedInputCombinatorBuilder<BoxingInput<*, OUT>, List<OUT>> {
    override fun compile(compiler: CombinatorBuilder.Compiler<BoxingInput<*, OUT>>): CompiledParser<BoxingInput<*, OUT>, List<OUT>> {
        val singleValueOption = compiler.compileToSingleValueParser(parser)
        return if (singleValueOption != null) {
            ZeroOrMoreSingleInputParser(singleValueOption)
        } else {
            val option = compiler.compile(parser)
            of(option, Empty())
        }
    }

    companion object {
        fun <IN, ITEM, OUT> of(option: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            val empty = EmptyCompiledParser<IN, ITEM, OUT>(initial)
            val nested = OptionCompiledParser(option, initial)
            return ChoiceParser.of(listOf(nested, empty))
        }
    }

    private class OptionCompiledParser<IN, ITEM, OUT>(
        val option: CompiledParser<IN, ITEM>,
        val previous: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {

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