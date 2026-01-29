package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class DecideParser<IN, INTERMEDIATE, OUT>(
    private val parser: Parser<IN, INTERMEDIATE>,
    private val factory: (INTERMEDIATE) -> Parser<IN, OUT>
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {

    override fun withNoResult(): Parser<IN, Unit> {
        return DecideParser(parser) { value -> DiscardParser(factory(value)) }
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val compiled = compiler.compile(parser)
        return DecideCompiledParser(compiled, factory, compiler)
    }

    internal class DecideCompiledParser<IN, INTERMEDIATE, OUT>(
        val parser: CompiledParser<IN, INTERMEDIATE>,
        private val factory: (INTERMEDIATE) -> Parser<*, OUT>,
        private val compiler: CombinatorBuilder.Compiler<IN>
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser.start { lengthA, valueA ->
                val nextParser: CompiledParser<IN, OUT> = compiler.compile(factory(valueA.get()))
                nextParser.start { lengthB, valueB ->
                    next.next(lengthA + lengthB, valueB)
                }
            }
        }
    }
}

/**
 * Returns a parser that applies the given parser then passes the result to the given factory to produce the next parser to apply.
 */
fun <IN, INTERMEDIATE, OUT> decide(parser: Parser<IN, INTERMEDIATE>, factory: (INTERMEDIATE) -> Parser<IN, OUT>): Parser<IN, OUT> {
    return DecideParser(parser, factory)
}