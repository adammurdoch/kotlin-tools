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
            return head.start(start, continuation(start, count - 1, initial, next))
        }

        private fun continuation(start: Position, remaining: Int, accumulator: Accumulator<ITEM, OUT>, next: ParseContinuation<IN, OUT>): ParseContinuation<IN, ITEM> {
            return if (remaining == 0) {
                next.map { length, value ->
                    val result = accumulator.add(value, length)
                    Pair(result.length, result)
                }
            } else {
                ParseContinuation.prefix { length, value ->
                    val result = accumulator.add(value, length)
                    val startNext = start + length
                    tail.start(startNext, continuation(startNext, remaining - 1, result, next))
                }
            }
        }
    }
}