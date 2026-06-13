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

    fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.Continuing<T>

    fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
        return PullParser.Failed(index, expected)
    }

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

            override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.Continuing<T> {
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
                    return PullParser.RequireMore(advance, parser, failedChoices)
                }

                override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
                    return PullParser.RequireMore(advance, parser, failedChoices)
                }
            }
        }
    }

    abstract class MappingParseContinuation<IN, INTERMEDIATE, OUT>(
        private val next: ParseContinuation<IN, OUT>,
    ) : ParseContinuation<IN, INTERMEDIATE> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<INTERMEDIATE>, failedChoices: List<PullParser.Failure>): PullParser.Result<IN> {
            val mappedValue = map(input, advance - length, advance, value)
            return next.matched(input, advance, length, mappedValue, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.Continuing<T> {
            return next.selected(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, length, expected)
        }

        protected abstract fun map(input: IN, start: Int, end: Int, value: ValueProvider<INTERMEDIATE>): ValueProvider<OUT>
    }

    abstract class FirstSegmentParseContinuation<IN, INTERMEDIATE, OUT>(
        protected val next: ParseContinuation<IN, OUT>,
    ) : ParseContinuation<IN, INTERMEDIATE> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<INTERMEDIATE>, failedChoices: List<PullParser.Failure>): PullParser.Result<IN> {
            val parser = map(input, length, value)
            return PullParser.RequireMore(advance, parser, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.Continuing<T> {
            return PullParser.RequireMore(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, length, expected)
        }

        protected abstract fun map(input: IN, length: Int, value: ValueProvider<INTERMEDIATE>): PullParser<IN>
    }

    private class EndParseContinuation<IN, OUT> : ParseContinuation<IN, OUT> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: List<PullParser.Failure>): PullParser.Matched<IN> {
            return PullParser.Matched(advance, EndPullParser, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.Continuing<T> {
            return PullParser.Matched(advance, parser, failedChoices)
        }
    }

    private object EndPullParser : PullParser<Any?> {
        override fun stop(input: Any?): PullParser.Failed {
            return PullParser.Failed(emptyList())
        }

        override fun parse(input: Any?, max: Int): PullParser.Result<Any?> {
            return PullParser.Matched(0, this, emptyList())
        }
    }
}
