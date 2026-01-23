package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class Sequence2Parser<IN, A, B, OUT>(
    private val a: Parser<IN, A>,
    private val b: Parser<IN, B>,
    private val map: (A, B) -> OUT
) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val compiledA = compiler.compile(a)
        val compiledB = compiler.compile(b)
        return Sequence2CompiledParser(compiledA, compiledB, map)
    }

    private class Sequence2CompiledParser<IN, A, B, OUT>(
        private val a: CompiledParser<IN, A>,
        private val b: CompiledParser<IN, B>,
        private val map: (A, B) -> OUT
    ) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean = a.mayNotAdvanceOnMatch && b.mayNotAdvanceOnMatch

        override val expectation: Expectation = if (a.mayNotAdvanceOnMatch) Expectation.OneOf.of(a.expectation, b.expectation) else a.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return a.start(b.expectation) { lengthA, valueA ->
                b.start(next.expectation) { lengthB, valueB ->
                    val value = map(valueA, valueB)
                    next.next(lengthA + lengthB, value)
                }
            }
        }
    }
}