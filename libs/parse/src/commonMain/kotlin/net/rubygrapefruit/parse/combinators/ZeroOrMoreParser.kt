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
            start: Position,
            option: CompiledParser<IN, ITEM>,
            tail: CompiledParser<IN, ITEM>,
            initial: Accumulator<ITEM, OUT>,
            next: ParseContinuation<IN, OUT>
        ): PullParser<IN> {
            val empty = EmptyCompiledParser<IN, ITEM, OUT>(initial)
            return ChoiceParser.of(
                start,
                listOf(
                    ChoiceParser.Option(option, OptionContinuation(start, tail, initial, next)),
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
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return of(start, option, tail, initial, next)
        }
    }

    internal class OptionContinuation<IN, ITEM, OUT>(
        private val start: Position,
        private val parser: CompiledParser<IN, ITEM>,
        private val previous: Accumulator<ITEM, OUT>,
        private val next: ParseContinuation<IN, OUT>
    ) : ParseContinuation<IN, ITEM> {
        override fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<ITEM>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<IN> {
            val result = previous.add(value, length)
            return if (length == 0) {
                next.matched(advance, commit, length, result, failedChoices)
            } else {
                val parser = of(start + length, parser, parser, result, next)
                PullParser.RequireMore(advance, false, parser, failedChoices)
            }
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
            return next.selected(advance, parser, failedChoices)
        }
    }

    internal class EmptyCompiledParser<IN, ITEM, OUT>(private val result: Accumulator<ITEM, OUT>) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
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