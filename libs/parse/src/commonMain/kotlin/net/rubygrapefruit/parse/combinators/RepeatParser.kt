package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser
import net.rubygrapefruit.parse.stream.Input

internal class RepeatParser<IN, OUT>(
    private val count: Int,
    private val item: Parser<IN, OUT>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return RepeatProduceNothingParser(count, DiscardParser(item), separator)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        return of(count, item, separator, compiler, ListAccumulator.Empty())
    }

    companion object {
        fun <IN, ITEM, OUT> of(
            count: Int,
            item: Parser<*, ITEM>,
            separator: Parser<*, Unit>?,
            compiler: CombinatorBuilder.Compiler<IN>,
            initial: Accumulator<ITEM, OUT>
        ): CompiledParser<IN, OUT> {
            return if (count == 0) {
                SucceedParser.SucceedCompiledParser(initial)
            } else {
                val head = compiler.compile(item)
                val tail = if (separator == null) {
                    head
                } else {
                    val tail = Sequence2Parser(separator, item) { _, v -> v }
                    compiler.compile(tail)
                }
                RepeatCompiledParser(count, head, tail, initial)
            }
        }
    }

    class RepeatCompiledParser<IN, ITEM, OUT>(
        val count: Int,
        val head: CompiledParser<IN, ITEM>,
        val tail: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return head.start(start, continuation(start, count - 1, 0, initial, next))
        }

        fun continuation(start: Position, remaining: Int, previousLength: Int, previous: Accumulator<ITEM, OUT>, next: ParseContinuation<IN, OUT>): ParseContinuation<IN, ITEM> {
            return if (remaining == 0) {
                LastParseContinuation(previousLength, previous, next)
            } else {
                MiddleParseContinuation(start, remaining, previousLength, previous, this, next)
            }
        }
    }

    private class MiddleParseContinuation<IN, ITEM, OUT>(
        private val start: Position,
        private val remaining: Int,
        previousLength: Int,
        private val previous: Accumulator<ITEM, OUT>,
        private val owner: RepeatCompiledParser<IN, ITEM, OUT>,
        next: ParseContinuation<IN, OUT>
    ) :
        ParseContinuation.MiddleSegmentParseContinuation<IN, ITEM, OUT>(previousLength, next) {
        override fun map(input: IN, length: Int, value: ValueProvider<ITEM>): PullParser<IN> {
            val result = previous.add(value)
            val startNext = start + length
            return owner.tail.start(startNext, owner.continuation(startNext, remaining - 1, previousLength + length, result, next))
        }
    }

    private class LastParseContinuation<IN, ITEM, OUT>(previousLength: Int, private val previous: Accumulator<ITEM, OUT>, next: ParseContinuation<IN, OUT>) :
        ParseContinuation.LastSegmentParseContinuation<IN, ITEM, OUT>(previousLength, next) {
        override fun map(length: Int, value: ValueProvider<ITEM>): ValueProvider<OUT> {
            return previous.add(value)
        }
    }
}