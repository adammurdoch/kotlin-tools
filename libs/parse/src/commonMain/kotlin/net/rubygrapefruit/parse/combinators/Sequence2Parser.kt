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
        override val mayNotAdvanceOnMatch: Boolean
            get() = a.mayNotAdvanceOnMatch && b.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = if (a.mayNotAdvanceOnMatch) Expectation.OneOf.of(a.expectation, b.expectation) else a.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return a.start { lengthA, valueA ->
                b.start { lengthB, valueB ->
                    val value = map(valueA, valueB)
                    next.next(lengthA + lengthB, value)
                }
            }
        }
    }
}