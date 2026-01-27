package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreParser<IN, OUT>(
    private val parser: Parser<IN, OUT>
) : Parser<IN, List<OUT>>, TypedInputCombinatorBuilder<BoxingInput<*, OUT>, List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return ZeroOrMoreProduceNothingParser(DiscardParser(parser))
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<BoxingInput<*, OUT>>): CompiledParser<BoxingInput<*, OUT>, List<OUT>> {
        val singleValueOption = compiler.maybeAsSingleInputParser(parser)
        return if (singleValueOption != null) {
            ZeroOrMoreSingleInputCompiledParser(singleValueOption, ListRangeAccumulator.Empty())
        } else {
            val option = compiler.compile(parser)
            of(option, ListAccumulator.Empty())
        }
    }

    companion object {
        fun <IN, ITEM, OUT> of(option: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            val empty = EmptyCompiledParser<IN, ITEM, OUT>(initial)
            val nested = OptionCompiledParser(option, initial)
            return ChoiceParser.of(listOf(nested, empty))
        }
    }

    internal class OptionCompiledParser<IN, ITEM, OUT>(
        val option: CompiledParser<IN, ITEM>,
        val previous: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {

        override val mayNotAdvanceOnMatch: Boolean
            get() = option.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = option.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return option.start { length, value ->
                val result = previous.add(value, length)
                if (length == 0) {
                    next.next(0, result.value)
                } else {
                    val empty = EmptyCompiledParser<IN, ITEM, OUT>(result)
                    val nested = OptionCompiledParser(option, result)
                    ChoiceParser.of(listOf(nested, empty), next)
                }
            }
        }
    }

    internal class EmptyCompiledParser<IN, ITEM, OUT>(private val result: Accumulator<ITEM, OUT>) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return next.next(result.length, result.value)
        }
    }
}