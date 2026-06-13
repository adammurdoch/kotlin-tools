package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

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
        override fun start(start: Position, next: ParseContinuation<IN, Unit>): PullParser<IN> {
            return parser.start(start, ConsumeParserContinuation(consumer, next))
        }
    }

    private class ConsumeParserContinuation<IN, OUT>(
        private val consumer: (OUT) -> Unit,
        next: ParseContinuation<IN, Unit>
    ) : ParseContinuation.MappingParseContinuation<IN, OUT, Unit>(next) {
        override fun map(input: IN, start: Int, end: Int, value: ValueProvider<OUT>): ValueProvider<Unit> {
            consumer(value.get())
            return ValueProvider.Nothing
        }
    }
}