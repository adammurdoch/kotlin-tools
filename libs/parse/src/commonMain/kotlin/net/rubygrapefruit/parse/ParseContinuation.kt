package net.rubygrapefruit.parse

internal interface ParseContinuation<in IN, in OUT, out NEXT> {
    val expectation: Expectation

    fun matched(start: Int, end: Int, value: OUT): PullParser.RequireMore<IN, NEXT> {
        return PullParser.RequireMore(end, next(end - start, value))
    }

    /**
     * Returns the next parser given a match.
     */
    fun next(length: Int, value: OUT): PullParser<IN, NEXT>

    companion object {
        fun <IN, OUT> end(): ParseContinuation<IN, OUT, OUT> {
            return EndParseContinuation()
        }

        fun <IN, OUT, NEXT> of(expectation: Expectation, next: (length: Int, value: OUT) -> PullParser<IN, NEXT>): ParseContinuation<IN, OUT, NEXT> {
            return object : ParseContinuation<IN, OUT, NEXT> {
                override val expectation: Expectation
                    get() = expectation

                override fun next(length: Int, value: OUT): PullParser<IN, NEXT> {
                    return next(length, value)
                }
            }
        }
    }

    private class EndParseContinuation<IN, OUT> : ParseContinuation<IN, OUT, OUT> {
        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun next(length: Int, value: OUT): PullParser<IN, OUT> {
            return EndMatchPullParser(value)
        }
    }

    private class EndMatchPullParser<IN, OUT>(val value: OUT) : PullParser<IN, OUT> {
        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun toString(): String {
            return "{end}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            return PullParser.Matched(value)
        }
    }
}