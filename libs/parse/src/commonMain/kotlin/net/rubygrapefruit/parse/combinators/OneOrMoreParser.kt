package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class OneOrMoreParser<IN, OUT>(
    private val parser: Parser<IN, OUT>
) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        TODO()
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        return OneOrMoreCompiledParser(compiler.compile(parser))
    }

    class OneOrMoreCompiledParser<IN, OUT>(val parser: CompiledParser<IN, OUT>) : CompiledParser<IN, List<OUT>> {
        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return parser.start(ParseContinuation.then { length, value ->
                val initial = ListAccumulator.Empty<OUT>().add(value, length)
                ZeroOrMoreParser.of(parser, initial).start(next)
            })
        }
    }
}