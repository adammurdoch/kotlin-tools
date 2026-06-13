package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

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
        override fun toString(): String {
            return "{sequence $a $b}"
        }

        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return a.start(start, AParseContinuation(start, b, map, next))
        }
    }

    private class AParseContinuation<IN, A, B, OUT>(
        private val start: Position,
        private val b: CompiledParser<IN, B>,
        private val map: (A, B) -> OUT,
        next: ParseContinuation<IN, OUT>
    ) : ParseContinuation.FirstSegmentParseContinuation<IN, A, OUT>(next) {
        override fun map(input: IN, length: Int, value: ValueProvider<A>): PullParser<IN> {
            return b.start(start + length, BParseContinuation(length, value, map, next))
        }
    }

    private class BParseContinuation<IN, A, B, OUT>(
        lengthA: Int,
        private val valueA: ValueProvider<A>,
        private val map: (A, B) -> OUT,
        next: ParseContinuation<IN, OUT>,
    ) : ParseContinuation.LastSegmentParseContinuation<IN, B, OUT>(lengthA, next) {
        override fun map(length: Int, value: ValueProvider<B>): ValueProvider<OUT> {
            return valueA.zip(value, map)
        }
    }
}