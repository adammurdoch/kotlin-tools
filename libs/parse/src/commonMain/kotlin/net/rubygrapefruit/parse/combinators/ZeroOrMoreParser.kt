package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreParser<IN, OUT>(
    private val parser: Parser<IN, OUT>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, List<OUT>>, TypedInputCombinatorBuilder<BoxingInput<*, OUT>, List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return ZeroOrMoreProduceNothingParser(DiscardParser(parser), separator)
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<BoxingInput<*, OUT>>): CompiledParser<BoxingInput<*, OUT>, List<OUT>> {
        if (separator == null) {
            val singleValueOption = compiler.maybeAsSingleInputParser(parser)
            if (singleValueOption != null) {
                return ZeroOrMoreSingleInputCompiledParser(singleValueOption, ListRangeAccumulator.Empty())
            }
        }

        val option = compiler.compile(parser)
        val tail = if (separator == null) {
            option
        } else {
            val tail = Sequence2Parser(separator, parser) { _, v -> v }
            compiler.compile(tail)
        }
        return of(option, tail, ListAccumulator.Empty())
    }

    companion object {
        fun <IN, ITEM, OUT> of(option: CompiledParser<IN, ITEM>, tail: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return ZeroOrMoreCompiledParser(option, tail, initial)
        }

        fun <IN, ITEM, OUT> of(
            option: CompiledParser<IN, ITEM>,
            tail: CompiledParser<IN, ITEM>,
            initial: Accumulator<ITEM, OUT>,
            next: ParseContinuation<IN, OUT>
        ): PullParser<IN> {
            val empty = EmptyCompiledParser<IN, ITEM, OUT>(initial)
            return ChoiceParser.of(
                listOf(
                    ChoiceParser.Option(option, OptionContinuation(tail, initial, next)),
                    ChoiceParser.Option(empty, next)
                )
            )
        }
    }

    class ZeroOrMoreCompiledParser<IN, ITEM, OUT>(
        val option: CompiledParser<IN, ITEM>,
        val tail: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return of(option, tail, initial, next)
        }
    }

    private class OptionContinuation<IN, ITEM, OUT>(
        private val parser: CompiledParser<IN, ITEM>,
        private val previous: Accumulator<ITEM, OUT>,
        private val next: ParseContinuation<IN, OUT>
    ) : ParseContinuation<IN, ITEM> {
        override val matches: Boolean
            get() = false

        override fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<ITEM>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN> {
            val result = previous.add(value, length)
            val parser = if (length == 0) {
                EmptyPullParser(result, next)
            } else {
                of(parser, parser, result, next)
            }
            return PullParser.RequireMore(advance, commit, false, parser, failedChoice)
        }
    }

    internal class EmptyCompiledParser<IN, ITEM, OUT>(private val result: Accumulator<ITEM, OUT>) : CompiledParser<IN, OUT> {
        override fun start(next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return EmptyPullParser(result, next)
        }
    }

    private class EmptyPullParser<IN, ITEM, OUT>(val result: Accumulator<ITEM, OUT>, val next: ParseContinuation<IN, OUT>) : PullParser<IN> {
        override fun toString(): String {
            return "{end-zero-or-more}"
        }

        override fun stop(): PullParser.Failed {
            return next.matched(-result.length, 0, result).parser.stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            return next.matched(-result.length, 0, result)
        }
    }
}