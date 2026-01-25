package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*

internal class SucceedParser<OUT>(
    private val result: OUT
) : Parser<Any, OUT>, CombinatorBuilder<OUT>, DiscardableParser<Any> {
    override fun withNoResult(): Parser<Any, Unit> {
        return SucceedParser(Unit)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return SucceedCompiledParser(result)
    }

    companion object {
        fun of(): Parser<Any, Unit> {
            return SucceedParser(Unit)
        }
    }

    internal class SucceedCompiledParser<IN, OUT>(
        val result: OUT
    ) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return next.next(0, result)
        }
    }
}