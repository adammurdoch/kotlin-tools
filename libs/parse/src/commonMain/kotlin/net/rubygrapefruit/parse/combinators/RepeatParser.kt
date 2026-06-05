package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser
import net.rubygrapefruit.parse.stream.Input

internal class RepeatParser<IN, OUT>(
    private val count: Int,
    private val parser: Parser<IN, OUT>
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return RepeatProduceNothingParser(count, DiscardParser(parser))
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        return of(count, parser, compiler, ListAccumulator.Empty())
    }

    companion object {
        fun <IN, ITEM, OUT> of(count: Int, parser: Parser<*, ITEM>, compiler: CombinatorBuilder.Compiler<IN>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return if (count == 0) {
                SucceedParser.SucceedCompiledParser(initial)
            } else {
                RepeatCompiledParser(count, compiler.compile(parser), initial)
            }
        }
    }

    class RepeatCompiledParser<IN, ITEM, OUT>(
        val count: Int,
        val parser: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return parser.start(start, continuation(start, count - 1, initial, next))
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
                    parser.start(startNext, continuation(startNext, remaining - 1, result, next))
                }
            }
        }
    }
}