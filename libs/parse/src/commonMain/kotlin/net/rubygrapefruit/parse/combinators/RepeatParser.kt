package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser

internal class RepeatParser<IN, OUT>(
    private val count: Int,
    private val parser: Parser<IN, OUT>
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return RepeatProduceNothingParser(count, DiscardParser(parser))
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        val initial = ListAccumulator.Empty<OUT>()
        return if (count == 0) {
            SucceedParser.SucceedCompiledParser(initial)
        } else {
            of(count, compiler.compile(parser), initial)
        }
    }

    companion object {
        fun <IN, ITEM, OUT> of(count: Int, parser: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return RepeatCompiledParser(count, parser, initial)
        }
    }

    class RepeatCompiledParser<IN, ITEM, OUT>(
        val count: Int,
        val parser: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser.start(continuation(count - 1, initial, next))
        }

        private fun <NEXT> continuation(count: Int, accumulator: Accumulator<ITEM, OUT>, next: ParseContinuation<IN, OUT, NEXT>): ParseContinuation<IN, ITEM, NEXT> {
            return if (count == 0) {
                next.map { length, value ->
                    val result = accumulator.add(value, length)
                    Pair(result.length, result)
                }
            } else {
                ParseContinuation.then { length, value ->
                    val result = accumulator.add(value, length)
                    parser.start(continuation(count - 1, result, next))
                }
            }
        }
    }
}