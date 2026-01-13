package net.rubygrapefruit.parse

internal interface ParseContinuation<IN, OUT, NEXT> {

    fun matched(count: Int, value: OUT): PullParser.Result<IN, NEXT>

    fun matched(match: PullParser.Matched<IN, OUT>): PullParser.Result<IN, NEXT>

    /**
     * Returns a parser that will succeed at the start of input with the given result.
     */
    fun succeed(result: OUT): PullParser<IN, NEXT> {
        return object : PullParser<IN, NEXT> {
            override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
                return matched(0, result)
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