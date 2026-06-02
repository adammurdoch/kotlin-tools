package net.rubygrapefruit.parse

internal interface ParseContinuation<in IN, in OUT> {
    val matches: Boolean

    fun matched(start: Int, end: Int, value: OUT): PullParser.RequireMore<IN> {
        return matched(start, end, ValueProvider.of(value), null)
    }

    fun matched(start: Int, end: Int, value: ValueProvider<OUT>): PullParser.RequireMore<IN> {
        return matched(start, end, value, null)
    }

    fun matched(start: Int, end: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN> {
        return PullParser.RequireMore(end, end, matches, next(end - start, value), failedChoice)
    }

    fun <T> map(map: (Int, ValueProvider<T>) -> Pair<Int, ValueProvider<OUT>>): ParseContinuation<IN, T> {
        val self = this
        return object : ParseContinuation<IN, T> {
            override val matches: Boolean
                get() = self.matches

            override fun toString(): String {
                return "{map $self}"
            }

            override fun next(length: Int, value: ValueProvider<T>): PullParser<IN> {
                val mapped = map(length, value)
                return self.next(mapped.first, mapped.second)
            }
        }
    }

    /**
     * Returns the next parser, given a match.
     */
    fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN>

    companion object {
        fun <IN, OUT> end(): ParseContinuation<IN, OUT> {
            return EndParseContinuation()
        }

        fun <IN, OUT> then(next: (length: Int, value: ValueProvider<OUT>) -> PullParser<IN>): ParseContinuation<IN, OUT> {
            return object : ParseContinuation<IN, OUT> {
                override val matches: Boolean
                    get() = false

                override fun toString(): String {
                    return "{then $next}"
                }

                override fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN> {
                    return next(length, value)
                }
            }
        }
    }

    private class EndParseContinuation<IN, OUT> : ParseContinuation<IN, OUT> {
        override val matches: Boolean
            get() = true

        override fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN> {
            return EndMatchPullParser
        }
    }

    private object EndMatchPullParser : PullParser<Any?> {
        override fun stop(): PullParser.Failed {
            return PullParser.Failed(0, Expectation.Nothing)
        }

        override fun toString(): String {
            return "{end}"
        }

        override fun parse(input: Any?, max: Int): PullParser.Result<Any?> {
            return PullParser.Matched
        }
    }
}

internal fun <IN> ParseContinuation<IN, Unit>.next(length: Int): PullParser<IN> {
    return next(length, ValueProvider.Nothing)
}