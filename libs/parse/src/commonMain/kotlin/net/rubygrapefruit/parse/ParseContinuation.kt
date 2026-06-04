package net.rubygrapefruit.parse

internal interface ParseContinuation<in IN, in OUT> {
    fun matched(start: Int, end: Int, value: OUT): PullParser.RequireMore<IN> {
        return matched(start, end, ValueProvider.of(value), null)
    }

    fun matched(start: Int, end: Int, value: ValueProvider<OUT>): PullParser.RequireMore<IN> {
        return matched(start, end, value, null)
    }

    fun matched(start: Int, end: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN> {
        return matched(end, end, end - start, value, failedChoice)
    }

    fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN>

    fun <T> selected(advance: Int, commit: Int, parser: PullParser<T>, failedChoice: ExpectationProvider?): PullParser.RequireMore<T>

    fun <T> map(map: (Int, ValueProvider<T>) -> Pair<Int, ValueProvider<OUT>>): ParseContinuation<IN, T> {
        val self = this
        return object : ParseContinuation<IN, T> {
            override fun toString(): String {
                return "{map $self}"
            }

            override fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<T>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN> {
                val mapped = map(length, value)
                return self.matched(advance, commit, mapped.first, mapped.second, failedChoice)
            }

            override fun <T> selected(advance: Int, commit: Int, parser: PullParser<T>, failedChoice: ExpectationProvider?): PullParser.RequireMore<T> {
                return self.selected(advance, commit, parser, failedChoice)
            }
        }
    }

    companion object {
        fun <IN, OUT> end(): ParseContinuation<IN, OUT> {
            return EndParseContinuation()
        }

        fun <IN, OUT> prefix(next: (length: Int, value: ValueProvider<OUT>) -> PullParser<IN>): ParseContinuation<IN, OUT> {
            return object : ParseContinuation<IN, OUT> {
                override fun toString(): String {
                    return "{then $next}"
                }

                override fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN> {
                    val parser = next(length, value)
                    return PullParser.RequireMore(advance, commit, false, parser, failedChoice)
                }

                override fun <T> selected(advance: Int, commit: Int, parser: PullParser<T>, failedChoice: ExpectationProvider?): PullParser.RequireMore<T> {
                    return PullParser.RequireMore(advance, commit, false, parser, failedChoice)
                }
            }
        }
    }

    private class EndParseContinuation<IN, OUT> : ParseContinuation<IN, OUT> {
        override fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN> {
            return PullParser.RequireMore(advance, commit, true, EndMatchPullParser, failedChoice)
        }

        override fun <T> selected(advance: Int, commit: Int, parser: PullParser<T>, failedChoice: ExpectationProvider?): PullParser.RequireMore<T> {
            return PullParser.RequireMore(advance, commit, true, parser, failedChoice)
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
