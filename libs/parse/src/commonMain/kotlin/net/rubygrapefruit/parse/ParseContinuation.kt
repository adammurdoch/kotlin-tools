package net.rubygrapefruit.parse

internal interface ParseContinuation<in IN, in OUT, out NEXT> {
    val matches: Boolean

    fun matched(start: Int, end: Int, value: OUT): PullParser.RequireMore<IN, NEXT> {
        return matched(start, end, ValueProvider.of(value), null)
    }

    fun matched(start: Int, end: Int, value: ValueProvider<OUT>): PullParser.RequireMore<IN, NEXT> {
        return matched(start, end, value, null)
    }

    fun matched(start: Int, end: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN, NEXT> {
        return PullParser.RequireMore(end, end, matches, next(end - start, value), failedChoice)
    }

    fun <T> map(map: (Int, ValueProvider<T>) -> Pair<Int, ValueProvider<OUT>>): ParseContinuation<IN, T, NEXT> {
        val self = this
        return object : ParseContinuation<IN, T, NEXT> {
            override val matches: Boolean
                get() = self.matches

            override fun toString(): String {
                return "{map $self}"
            }

            override fun next(length: Int, value: ValueProvider<T>): PullParser<IN, NEXT> {
                val mapped = map(length, value)
                return self.next(mapped.first, mapped.second)
            }
        }
    }

    /**
     * Returns the next parser, given a match.
     */
    fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN, NEXT>

    companion object {
        fun <IN, OUT> end(): ParseContinuation<IN, OUT, OUT> {
            return EndParseContinuation()
        }

        fun <IN, OUT, NEXT> then(next: (length: Int, value: ValueProvider<OUT>) -> PullParser<IN, NEXT>): ParseContinuation<IN, OUT, NEXT> {
            return object : ParseContinuation<IN, OUT, NEXT> {
                override val matches: Boolean
                    get() = false

                override fun toString(): String {
                    return "{then $next}"
                }

                override fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN, NEXT> {
                    return next(length, value)
                }
            }
        }
    }

    private class EndParseContinuation<IN, OUT> : ParseContinuation<IN, OUT, OUT> {
        override val matches: Boolean
            get() = true

        override fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN, OUT> {
            return EndMatchPullParser(value)
        }
    }

    private class EndMatchPullParser<IN, OUT>(val value: ValueProvider<OUT>) : PullParser<IN, OUT> {
        override fun stop(): PullParser.Failed {
            return PullParser.Failed(0, Expectation.Nothing)
        }

        override fun toString(): String {
            return "{end}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            return PullParser.Matched(value.get())
        }
    }
}

internal fun <IN, NEXT> ParseContinuation<IN, Unit, NEXT>.next(length: Int): PullParser<IN, NEXT> {
    return next(length, ValueProvider.Nothing)
}