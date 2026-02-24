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
        fun <IN, ITEM, OUT> of(parser: CompiledParser<IN, ITEM>, empty: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return OneOrMoreCompiledParser(parser, empty)
        }
    }

    class OneOrMoreCompiledParser<IN, ITEM, OUT>(val parser: CompiledParser<IN, ITEM>, val empty: Accumulator<ITEM, OUT>) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser.start(ParseContinuation.then { length, value ->
                val result = empty.add(value, length)
                ZeroOrMoreParser.of(parser, result).start(next)
            })
        }
    }
}