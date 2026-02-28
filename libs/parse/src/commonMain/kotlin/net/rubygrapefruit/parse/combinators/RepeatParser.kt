package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class RepeatParser<IN, OUT>(
    private val count: Int,
    private val parser: Parser<IN, OUT>
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        return RepeatCompiledParser(count, compiler.compile(parser))
    }

    class RepeatCompiledParser<IN, OUT>(
        val count: Int,
        val parser: CompiledParser<IN, OUT>
    ) : CompiledParser<IN, List<OUT>> {
        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return parser.start(continuation(count - 1, ListAccumulator.Empty(), next))
        }

        private fun <NEXT> continuation(count: Int, accumulator: Accumulator<OUT, List<OUT>>, next: ParseContinuation<IN, List<OUT>, NEXT>): ParseContinuation<IN, OUT, NEXT> {
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