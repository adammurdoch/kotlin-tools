package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreParser<IN, OUT>(
    private val parser: Parser<IN, OUT>
) : Parser<IN, List<OUT>>, TypedInputCombinatorBuilder<BoxingInput<*, OUT>, List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return ZeroOrMoreProduceNothingParser(DiscardParser(this@ZeroOrMoreParser.parser))
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<BoxingInput<*, OUT>>): CompiledParser<BoxingInput<*, OUT>, List<OUT>> {
        val singleValueOption = compiler.maybeAsSingleInputParser(this@ZeroOrMoreParser.parser)
        return if (singleValueOption != null) {
            ZeroOrMoreSingleInputCompiledParser(singleValueOption, ListRangeAccumulator.Empty())
        } else {
            val option = compiler.compile(this@ZeroOrMoreParser.parser)
            of(option, ListAccumulator.Empty())
        }
    }

    companion object {
        fun <IN, ITEM, OUT> of(parser: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return ZeroOrMoreCompiledParser(parser, initial)
        }

        fun <IN, ITEM, OUT, NEXT> of(parser: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            val empty = EmptyCompiledParser<IN, ITEM, OUT>(initial)
            val nested = OptionCompiledParser(parser, initial)
            return ChoiceParser.of(listOf(nested, empty), next)
        }
    }

    class ZeroOrMoreCompiledParser<IN, ITEM, OUT>(
        val parser: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return of(parser, initial, next)
        }
    }

    private class OptionContinuation<IN, ITEM, OUT, NEXT>(
        private val parser: CompiledParser<IN, ITEM>,
        private val previous: Accumulator<ITEM, OUT>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : ParseContinuation<IN, ITEM, NEXT> {
        override val matches: Boolean
            get() = false

        override fun next(length: Int, value: ValueProvider<ITEM>): PullParser<IN, NEXT> {
            val result = previous.add(value, length)
            return if (length == 0) {
                EmptyPullParser(result, next)
            } else {
                of(parser, result, next)
            }
        }
    }

    private class OptionCompiledParser<IN, ITEM, OUT>(
        val parser: CompiledParser<IN, ITEM>,
        val previous: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser.start(OptionContinuation(parser, previous, next))
        }
    }

    internal class EmptyCompiledParser<IN, ITEM, OUT>(private val result: Accumulator<ITEM, OUT>) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return EmptyPullParser(result, next)
        }
    }

    private class EmptyPullParser<IN, ITEM, OUT, NEXT>(val result: Accumulator<ITEM, OUT>, val next: ParseContinuation<IN, OUT, NEXT>) : PullParser<IN, NEXT> {
        override fun toString(): String {
            return "{end-zero-or-more}"
        }

        override fun stop(): PullParser.Failed {
            return next.next(result.length, result).stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return next.matched(-result.length, 0, result)
        }
    }
}