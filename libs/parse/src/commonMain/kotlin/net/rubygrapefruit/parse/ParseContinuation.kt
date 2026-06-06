package net.rubygrapefruit.parse

internal interface ParseContinuation<in IN, in OUT> {
    fun matched(input: IN, advance: Int, length: Int, value: OUT): PullParser.Result<IN> {
        return matched(input, advance, length, ValueProvider.of(value), emptyList())
    }

    fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>): PullParser.Result<IN> {
        return matched(input, advance, length, value, emptyList())
    }

    fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider): PullParser.Result<IN> {
        return matched(input, advance, length, value, listOf(PullParser.Failure(advance, failedChoice)))
    }

    fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: List<PullParser.Failure>): PullParser.Result<IN>

    fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T>

    fun <T> map(map: (Int, ValueProvider<T>) -> Pair<Int, ValueProvider<OUT>>): ParseContinuation<IN, T> {
        val self = this
        return object : ParseContinuation<IN, T> {
            override fun toString(): String {
                return "{map $self}"
            }

            override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<T>, failedChoices: List<PullParser.Failure>): PullParser.Result<IN> {
                val mapped = map(length, value)
                return self.matched(input, advance, mapped.first, mapped.second, failedChoices)
            }

            override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
                return self.selected(advance, parser, failedChoices)
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

                override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<IN> {
                    val parser = next(length, value)
                    return PullParser.RequireMore(advance, false, parser, failedChoices)
                }

                override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
                    return PullParser.RequireMore(advance, false, parser, failedChoices)
                }
            }
        }
    }

    private class EndParseContinuation<IN, OUT> : ParseContinuation<IN, OUT> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<IN> {
            return PullParser.RequireMore(advance, true, EndMatchPullParser, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
            return PullParser.RequireMore(advance, true, parser, failedChoices)
        }
    }

    private object EndMatchPullParser : PullParser<Any?> {
        override fun stop(input: Any?): PullParser.Failed {
            return PullParser.Failed(emptyList())
        }

        override fun toString(): String {
            return "{end}"
        }

        override fun parse(input: Any?, max: Int): PullParser.Result<Any?> {
            return PullParser.Matched
        }
    }
}
