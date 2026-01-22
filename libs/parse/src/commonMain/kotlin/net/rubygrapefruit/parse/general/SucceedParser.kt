package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*

internal class SucceedParser<IN, OUT>(private val result: OUT) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return SucceedCompiledParser(result)
    }

    companion object {
        fun <IN> of(): Parser<IN, Unit> {
            return SucceedParser(Unit)
        }

        fun <IN, NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return start(Unit, next)
        }

        fun <IN, OUT, NEXT> start(result: OUT, next: ParseContinuation<IN, OUT, NEXT>, length: Int = 0): PullParser<IN, NEXT> {
            return next.next(length, result)
        }
    }

    private class SucceedCompiledParser<IN, OUT>(
        val result: OUT
    ) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return start(result, next)
        }
    }
}