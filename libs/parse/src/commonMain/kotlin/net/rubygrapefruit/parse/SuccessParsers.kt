package net.rubygrapefruit.parse

/**
 * Returns a parser that always succeeds.
 */
fun succeed(): Parser<Any, Unit> {
    return SucceedParser(Unit)
}

/**
 * Returns a parser that always succeeds and produces the given result.
 */
fun <OUT> succeed(result: OUT): Parser<Any, OUT> {
    return SucceedParser(result)
}

private class SucceedParser<OUT>(private val result: OUT) : Parser<Any, OUT>, ParserBuilder<Any, OUT> {
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