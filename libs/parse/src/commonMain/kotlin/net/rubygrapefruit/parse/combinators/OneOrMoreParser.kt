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
        if (separator == null) {
            val singleInputParser = compiler.maybeAsSingleInputParser(item)
            if (singleInputParser != null) {
                return RepeatingSingleInputCompiledParser(Range.OneOrMoreMore, singleInputParser.predicate, singleInputParser.expectation, ListRangeAccumulator.Empty(singleInputParser.extractor))
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
            return OneOrMoreCompiledParser(head, tail, initial)
        }
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