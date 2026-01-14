package net.rubygrapefruit.parse

internal interface ParseContinuation<IN, OUT, NEXT> {

    fun matched(count: Int, value: OUT): PullParser.Result<IN, NEXT>

    fun matched(match: PullParser.Matched<IN, OUT>): PullParser.Result<IN, NEXT>

    /**
     * Returns a parser that succeeds at the start of input with the given result.
     */
    fun succeed(result: OUT): PullParser<IN, NEXT> {
        val state = matched(0, result)
        return if (state is PullParser.RequireMore) {
            state.parser
        } else {
            object : PullParser<IN, NEXT> {
                override val expected: Expectation
                    get() = Expectation.Nothing

                override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
                    return state
                }
            }
        }
    }

    companion object {
        fun <IN, OUT> of(): ParseContinuation<IN, OUT, OUT> {
            return of { it }
        }

        fun <IN, OUT, NEXT> of(next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): ParseContinuation<IN, OUT, NEXT> {
            return object : ParseContinuation<IN, OUT, NEXT> {
                override fun matched(count: Int, value: OUT): PullParser.Result<IN, NEXT> {
                    return next(PullParser.Matched(count, value))
                }

                override fun matched(match: PullParser.Matched<IN, OUT>): PullParser.Result<IN, NEXT> {
                    return next(match)
                }
            }
        }
    }
}