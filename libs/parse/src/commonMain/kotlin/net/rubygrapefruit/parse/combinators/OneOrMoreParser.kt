package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class OneOrMoreParser<IN, OUT>(
    private val parser: Parser<IN, OUT>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return OneOrMoreProduceNothingParser(DiscardParser(parser), separator)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        val option = compiler.compile(parser)
        val tail = if (separator == null) {
            option
        } else {
            val tail = Sequence2Parser(separator, parser) { _, b -> b }
            compiler.compile(tail)
        }
        return OneOrMoreCompiledParser(option, tail, ListAccumulator.Empty())
    }

    companion object {
        fun <IN, ITEM, OUT> of(parser: CompiledParser<IN, ITEM>, tail: CompiledParser<IN, ITEM>, initial: Accumulator<ITEM, OUT>): CompiledParser<IN, OUT> {
            return OneOrMoreCompiledParser(parser, tail, initial)
        }
    }

    class OneOrMoreCompiledParser<IN, ITEM, OUT>(
        val parser: CompiledParser<IN, ITEM>,
        val tail: CompiledParser<IN, ITEM>,
        val initial: Accumulator<ITEM, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser.start(ParseContinuation.then { length, value ->
                val result = initial.add(value, length)
                if (length == 0) {
                    next.next(result.length, result)
                } else {
                    ZeroOrMoreParser.of(tail, tail, result, next)
                }
            })
        }
    }
}