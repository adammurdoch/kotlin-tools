package net.rubygrapefruit.parse

internal interface ParseContinuation<IN, OUT, NEXT> {
    fun matched(count: Int, value: OUT): PullParser.Result<IN, NEXT>
    fun matched(match: PullParser.Matched<IN, OUT>): PullParser.Result<IN, NEXT>

    companion object {
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