package net.rubygrapefruit.parse

internal class SucceedParser<IN, OUT>(private val result: OUT) : Parser<IN, OUT>, ParserBuilder<IN, OUT> {
    override fun <NEXT> build(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return SucceedPullParser(result, next)
    }

    companion object {
        fun <IN> of(): Parser<IN, Unit> {
            return SucceedParser(Unit)
        }
    }

    private class SucceedPullParser<IN, OUT, NEXT>(
        private val result: OUT,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        override fun toString(): String {
            return "{succeed}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return next.matched(0, result)
        }
    }
}