package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ConsumeParser<IN, OUT>(
    private val parser: Parser<IN, OUT>,
    private val consumer: (OUT) -> Unit
) : Parser<IN, Unit>, CombinatorBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return DiscardParser(parser)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return ConsumeCompiledParser(compiler.compile(parser), consumer)
    }

    internal class ConsumeCompiledParser<IN, OUT>(val parser: CompiledParser<IN, OUT>, private val consumer: (OUT) -> Unit) : CompiledParser<IN, Unit> {
        override fun <NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return parser.start(next.map { length, value ->
                consumer(value.get())
                Pair(length, ValueProvider.Nothing)
            })
        }
    }
}