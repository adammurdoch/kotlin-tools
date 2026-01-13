package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class Sequence2Parser<IN, A, B, OUT>(
    private val a: Parser<IN, A>,
    private val b: Parser<IN, B>,
    private val map: (A, B) -> OUT
) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(converter: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val compiledA = converter.compile(a)
        val compiledB = converter.compile(b)
        return Sequence2CompiledParser(compiledA, compiledB, map)
    }

    private class Sequence2CompiledParser<IN, A, B, OUT>(
        private val a: CompiledParser<IN, A>,
        private val b: CompiledParser<IN, B>,
        private val map: (A, B) -> OUT
    ) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = a.mayNotAdvanceOnMatch && b.mayNotAdvanceOnMatch

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return a.start { resultA ->
                val parserB = b.start { resultB ->
                    val result = map(resultA.value, resultB.value)
                    next.matched(resultB.count, result)
                }
                PullParser.RequireMore(resultA.count, parserB)
            }
        }
    }
}