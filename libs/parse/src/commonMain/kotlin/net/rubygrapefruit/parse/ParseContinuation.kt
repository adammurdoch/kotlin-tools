package net.rubygrapefruit.parse

internal interface ParseContinuation<in IN, in OUT, out NEXT> {

    fun matched(start: Int, end: Int, value: OUT): PullParser.RequireMore<IN, NEXT> {
        return PullParser.RequireMore(end, next(end - start, value))
    }

    fun next(length: Int, value: OUT): PullParser<IN, NEXT>

    companion object {
        fun <IN, OUT> end(): ParseContinuation<IN, OUT, OUT> {
            return object : ParseContinuation<IN, OUT, OUT> {
                override fun next(length: Int, value: OUT): PullParser<IN, OUT> {
                    return object : PullParser<IN, OUT> {
                        override val expectation: Expectation
                            get() = Expectation.Nothing

                        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
                            return PullParser.Matched(-length, 0, value)
                        }
                    }
                }
            }
        }

        fun <IN, OUT, NEXT> of(next: (length: Int, value: OUT) -> PullParser<IN, NEXT>): ParseContinuation<IN, OUT, NEXT> {
            return object : ParseContinuation<IN, OUT, NEXT> {
                override fun next(length: Int, value: OUT): PullParser<IN, NEXT> {
                    return next(length, value)
                }
            }
        }
    }
}