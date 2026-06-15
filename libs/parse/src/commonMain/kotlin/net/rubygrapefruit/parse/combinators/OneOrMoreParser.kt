package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class OneOrMoreParser<IN, OUT>(
    private val item: Parser<IN, OUT>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return OneOrMoreProduceNothingParser(DiscardParser(item), separator)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        return rangeParser(Range.OneOrMoreMore, item, separator, compiler)
    }

    class OneOrMoreCompiledParser<IN, ITEM, OUT>(
        val head: CompiledParser<IN, ITEM>,
        val tail: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return head.start(start, ZeroOrMoreParser.ItemContinuation(start, 0, initial, tail, next))
        }
    }
}