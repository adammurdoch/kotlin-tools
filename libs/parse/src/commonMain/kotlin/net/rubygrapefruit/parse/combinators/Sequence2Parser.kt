package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class Sequence2Parser<IN, A, B, OUT>(
    private val a: Parser<IN, A>,
    private val b: Parser<IN, B>,
    private val map: (A, B) -> OUT
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return Sequence2Parser(DiscardParser(a), DiscardParser(b)) { _, _ -> }
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val compiledA = compiler.compile(a)
        val compiledB = compiler.compile(b)
        return Sequence2CompiledParser(compiledA, compiledB, map)
    }

    internal class Sequence2CompiledParser<IN, A, B, OUT>(
        val a: CompiledParser<IN, A>,
        val b: CompiledParser<IN, B>,
        private val map: (A, B) -> OUT
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return a.start(FirstContinuation(b, map, next))
        }
    }

    private class FirstContinuation<IN, A, B, OUT, NEXT>(
        val b: CompiledParser<IN, B>,
        val map: (A, B) -> OUT,
        val next: ParseContinuation<IN, OUT, NEXT>
    ) : ParseContinuation<IN, A, NEXT> {
        override fun matched(start: Int, end: Int, value: ValueProvider<A>): PullParser.RequireMore<IN, NEXT> {
            return PullParser.RequireMore(end, false, next(end - start, value))
        }

        override fun next(length: Int, value: ValueProvider<A>): PullParser<IN, NEXT> {
            return b.start { lengthB, valueB ->
                val value = value.zip(valueB, map)
                next.next(length + lengthB, value)
            }
        }
    }
}