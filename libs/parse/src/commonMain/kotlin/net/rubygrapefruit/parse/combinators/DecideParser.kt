package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class DecideParser<IN, INTERMEDIATE, OUT>(
    private val parser: Parser<IN, INTERMEDIATE>,
    private val factory: (INTERMEDIATE) -> Parser<IN, OUT>
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {

    override fun withNoResult(): Parser<IN, Unit> {
        return DecideParser(parser) { value -> DiscardParser(factory(value)) }
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return DecideCompiledParser(compiler.compile(parser), factory, compiler)
    }

    internal class DecideCompiledParser<IN, INTERMEDIATE, OUT>(
        val parser: CompiledParser<IN, INTERMEDIATE>,
        private val factory: (INTERMEDIATE) -> Parser<*, OUT>,
        private val compiler: CombinatorBuilder.Compiler<IN>
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return parser.start(start, FirstParseContinuation(start, factory, compiler, next))
        }
    }

    private class FirstParseContinuation<IN, INTERMEDIATE, OUT>(
        private val start: Position,
        private val factory: (INTERMEDIATE) -> Parser<*, OUT>,
        private val compiler: CombinatorBuilder.Compiler<IN>,
        next: ParseContinuation<IN, OUT>
    ) : ParseContinuation.FirstSegmentParseContinuation<IN, INTERMEDIATE, OUT>(next) {
        override fun map(input: IN, length: Int, value: ValueProvider<INTERMEDIATE>): PullParser<IN> {
            val nextParser: CompiledParser<IN, OUT> = compiler.compile(factory(value.get()))
            return nextParser.start(start + length, SecondParseContinuation(length, next))
        }
    }

    private class SecondParseContinuation<IN, OUT>(lengthA: Int, next: ParseContinuation<IN, OUT>) : ParseContinuation.LastSegmentParseContinuation<IN, OUT, OUT>(lengthA, next) {
        override fun map(length: Int, value: ValueProvider<OUT>): ValueProvider<OUT> {
            return value
        }
    }
}

/**
 * Returns a parser that applies the given parser then passes the result to the given factory to produce the next parser to apply.
 */
fun <IN, INTERMEDIATE, OUT> decide(parser: Parser<IN, INTERMEDIATE>, factory: (INTERMEDIATE) -> Parser<IN, OUT>): Parser<IN, OUT> {
    return DecideParser(parser, factory)
}