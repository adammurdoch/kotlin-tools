package net.rubygrapefruit.parse

internal class SucceedParser<OUT>(private val result: OUT) : Parser<Any, OUT>, ParserBuilder<Any, OUT> {
    override fun <NEXT> build(next: ParseContinuation<Any, OUT, NEXT>): PullParser<Any, NEXT> {
        return SucceedPullParser(result, next)
    }

    private class SucceedPullParser<OUT, NEXT>(
        private val result: OUT,
        private val next: ParseContinuation<Any, OUT, NEXT>
    ) : PullParser<Any, NEXT> {
        override fun toString(): String {
            return "{succeed}"
        }

        override fun parse(input: Any, max: Int): PullParser.Result<Any, NEXT> {
            return next.matched(0, result)
        }
    }
}