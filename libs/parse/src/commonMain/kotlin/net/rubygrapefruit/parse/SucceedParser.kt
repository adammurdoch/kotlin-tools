package net.rubygrapefruit.parse

internal class SucceedParser<IN, OUT>(private val result: OUT) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(converter: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return SucceedCompiledParser(result)
    }

    companion object {
        fun <IN> of(): Parser<IN, Unit> {
            return SucceedParser(Unit)
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
            return SucceedPullParser(result, next)
        }
    }

    private class SucceedPullParser<IN, OUT, NEXT>(
        private val result: OUT,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        override val expected: Expectation
            get() = Expectation.Nothing

        override fun toString(): String {
            return "{succeed}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return next.matched(0, result)
        }
    }
}