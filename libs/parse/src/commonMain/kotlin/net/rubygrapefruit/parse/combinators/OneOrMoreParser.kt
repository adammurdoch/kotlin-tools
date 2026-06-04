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
        return of(option, tail, ListAccumulator.Empty())
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
        override fun start(next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return parser.start(ParseContinuation.suffix { length, value ->
                val result = initial.add(value, length)
                if (length == 0) {
                    next.matched(0, result.length, result).parser
                } else {
                    ZeroOrMoreParser.of(tail, tail, result, next)
                }
            })
        }
    }
}