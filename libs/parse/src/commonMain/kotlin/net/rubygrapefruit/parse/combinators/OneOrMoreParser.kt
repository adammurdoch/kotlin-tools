package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class OneOrMoreParser<IN, OUT>(
    private val parser: Parser<IN, OUT>
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return OneOrMoreProduceNothingParser(DiscardParser(parser))
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        return of(compiler.compile(parser), ListAccumulator.Empty())
    }

    companion object {
        fun <IN, ITEM, OUT> of(parser: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return OneOrMoreCompiledParser(parser, initial)
        }
    }

    class OneOrMoreCompiledParser<IN, ITEM, OUT>(
        val parser: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser.start(ParseContinuation.then { length, value ->
                val result = initial.add(value, length)
                if (length == 0) {
                    next.next(result.length, result)
                } else {
                    ZeroOrMoreParser.of(parser, parser, result, next)
                }
            })
        }
    }
}