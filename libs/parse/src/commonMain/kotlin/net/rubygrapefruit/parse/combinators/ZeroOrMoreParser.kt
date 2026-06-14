package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.BoxingInput

internal class ZeroOrMoreParser<IN, OUT>(
    private val item: Parser<IN, OUT>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, List<OUT>>, TypedInputCombinatorBuilder<BoxingInput<*, OUT>, List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return ZeroOrMoreProduceNothingParser(DiscardParser(item), separator)
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<BoxingInput<*, OUT>>): CompiledParser<BoxingInput<*, OUT>, List<OUT>> {
        if (separator == null) {
            val singleValueItem = compiler.maybeAsSingleInputParser(item)
            if (singleValueItem != null) {
                return ZeroOrMoreSingleInputCompiledParser(singleValueItem, ListRangeAccumulator.Empty(NextValueExtractor.of()))
            }
        }

        return of(item, separator, compiler, ListAccumulator.Empty())
    }

    companion object {
        fun <IN, ITEM, OUT> of(
            item: Parser<*, ITEM>,
            separator: Parser<*, Unit>?,
            compiler: CombinatorBuilder.Compiler<IN>,
            initial: Accumulator<ITEM, OUT>
        ): CompiledParser<IN, OUT> {
            val head = compiler.compile(item)
            val tail = if (separator == null) {
                head
            } else {
                val tail = Sequence2Parser(separator, item) { _, v -> v }
                compiler.compile(tail)
            }
            return ZeroOrMoreCompiledParser(head, tail, initial)
        }

        fun <IN, ITEM, OUT> of(
            start: Position,
            head: CompiledParser<IN, ITEM>,
            tail: CompiledParser<IN, ITEM>,
            previousLength: Int,
            previous: Accumulator<ITEM, OUT>,
            next: ParseContinuation<IN, OUT>
        ): PullParser<IN> {
            val empty = EmptyCompiledParser<IN, OUT>(previousLength, previous)
            return ChoiceParser.of(
                start,
                listOf(
                    ChoiceParser.Option(head, ItemContinuation(start, previousLength, previous, tail, next)),
                    ChoiceParser.Option(empty, next)
                )
            )
        }
    }

    class ZeroOrMoreCompiledParser<IN, ITEM, OUT>(
        val head: CompiledParser<IN, ITEM>,
        val tail: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return of(start, head, tail, 0, initial, next)
        }
    }

    internal class ItemContinuation<IN, ITEM, OUT>(
        private val start: Position,
        private val previousLength: Int,
        private val previous: Accumulator<ITEM, OUT>,
        private val parser: CompiledParser<IN, ITEM>,
        private val next: ParseContinuation<IN, OUT>
    ) : ParseContinuation<IN, ITEM> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<ITEM>, failedChoices: List<PullParser.Failure>): PullParser.Result<IN> {
            val result = previous.add(value)
            return if (length == 0) {
                next.matched(input, advance, length, result, failedChoices)
            } else {
                val parser = of(start + length, parser, parser, previousLength + length, result, next)
                PullParser.RequireMore(advance, parser, failedChoices)
            }
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.Continuing<T> {
            return next.selected(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, previousLength + length, expected)
        }
    }

    internal class EmptyCompiledParser<IN, OUT>(val length: Int, val result: ValueProvider<OUT>) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return EmptyPullParser(length, result, next)
        }
    }

    private class EmptyPullParser<IN, OUT>(val length: Int, val result: ValueProvider<OUT>, val next: ParseContinuation<IN, OUT>) : PullParser<IN> {
        override fun toString(): String {
            return "{end-zero-or-more}"
        }

        override fun stop(input: IN): PullParser.Failed {
            return next.matched(input, 0, length, result).stop(input)
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            return next.matched(input, 0, length, result)
        }
    }
}