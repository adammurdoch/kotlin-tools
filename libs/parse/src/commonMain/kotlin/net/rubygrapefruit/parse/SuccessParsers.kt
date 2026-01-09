package net.rubygrapefruit.parse

/**
 * Returns a parser that always succeeds.
 */
fun succeed(): Parser<Any, Unit> {
    return SuccessParser(Unit)
}

/**
 * Returns a parser that always succeeds and produces the given result.
 */
fun <OUT> succeed(result: OUT): Parser<Any, OUT> {
    return SuccessParser(result)
}

private class SuccessParser<OUT>(private val result: OUT) : Parser<Any, OUT>, ParserBuilder<Any, OUT> {
    override fun <NEXT> build(next: ParseContinuation<Any, OUT, NEXT>): PullParser<Any, NEXT> {
        return SuccessPullParser(result, next)
    }

    private class SuccessPullParser<OUT, NEXT>(
        private val result: OUT,
        private val next: ParseContinuation<Any, OUT, NEXT>
    ) : PullParser<Any, NEXT> {
        override fun parse(input: Any, max: Int): PullParser.Result<Any, NEXT> {
            return next.matched(0, result)
        }
    }
}