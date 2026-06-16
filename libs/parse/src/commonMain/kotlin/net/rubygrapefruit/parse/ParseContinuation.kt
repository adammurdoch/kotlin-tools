package net.rubygrapefruit.parse

internal interface ParseContinuation<in IN, in OUT> {
    fun matched(input: IN, advance: Int, length: Int, value: OUT): PullParser.Result<IN> {
        return matched(input, advance, length, ValueProvider.of(value), PullParser.Failed.None)
    }

    fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>): PullParser.Result<IN> {
        return matched(input, advance, length, value, PullParser.Failed.None)
    }

    fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider): PullParser.Result<IN> {
        return matched(input, advance, length, value, PullParser.Failed.One(advance, failedChoice))
    }

    fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: PullParser.Failed): PullParser.Result<IN>

    fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: PullParser.Failed): PullParser.Continuing<T>

    fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
        return PullParser.Failed.One(index, expected)
    }

    companion object {
        fun <IN, OUT> end(): ParseContinuation<IN, OUT> {
            return EndParseContinuation()
        }
    }

    abstract class WrappingParseContinuation<IN, INTERMEDIATE, OUT>(
        protected val next: ParseContinuation<IN, OUT>,
    ) : ParseContinuation<IN, INTERMEDIATE> {
        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: PullParser.Failed): PullParser.Continuing<T> {
            return next.selected(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, length, expected)
        }
    }

    /**
     * Continuation for a parser that wraps another and modifies its result.
     */
    abstract class MappingParseContinuation<IN, INTERMEDIATE, OUT>(
        next: ParseContinuation<IN, OUT>,
    ) : WrappingParseContinuation<IN, INTERMEDIATE, OUT>(next) {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<INTERMEDIATE>, failedChoices: PullParser.Failed): PullParser.Result<IN> {
            val mappedValue = map(input, advance - length, advance, value)
            return next.matched(input, advance, length, mappedValue, failedChoices)
        }

        protected abstract fun map(input: IN, start: Int, end: Int, value: ValueProvider<INTERMEDIATE>): ValueProvider<OUT>
    }

    /**
     * Continuation for a parser that is the first in a sequence.
     */
    abstract class FirstSegmentParseContinuation<IN, INTERMEDIATE, OUT>(
        protected val next: ParseContinuation<IN, OUT>,
    ) : ParseContinuation<IN, INTERMEDIATE> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<INTERMEDIATE>, failedChoices: PullParser.Failed): PullParser.Result<IN> {
            val parser = map(input, length, value)
            return PullParser.RequireMore(advance, parser, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: PullParser.Failed): PullParser.Continuing<T> {
            return PullParser.RequireMore(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, length, expected)
        }

        protected abstract fun map(input: IN, length: Int, value: ValueProvider<INTERMEDIATE>): PullParser<IN>
    }

    /**
     * Continuation for a parser that is in the middle in a sequence.
     */
    abstract class MiddleSegmentParseContinuation<IN, INTERMEDIATE, OUT>(
        protected val previousLength: Int,
        protected val next: ParseContinuation<IN, OUT>,
    ) : ParseContinuation<IN, INTERMEDIATE> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<INTERMEDIATE>, failedChoices: PullParser.Failed): PullParser.Result<IN> {
            val parser = map(input, length, value)
            return PullParser.RequireMore(advance, parser, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: PullParser.Failed): PullParser.Continuing<T> {
            return PullParser.RequireMore(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, previousLength + length, expected)
        }

        protected abstract fun map(input: IN, length: Int, value: ValueProvider<INTERMEDIATE>): PullParser<IN>
    }

    /**
     * Continuation for a parser that is the last in a sequence.
     */
    abstract class LastSegmentParseContinuation<IN, INTERMEDIATE, OUT>(
        private val previousLength: Int,
        private val next: ParseContinuation<IN, OUT>,
    ) : ParseContinuation<IN, INTERMEDIATE> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<INTERMEDIATE>, failedChoices: PullParser.Failed): PullParser.Result<IN> {
            val mappedValue = map(length, value)
            return next.matched(input, advance, previousLength + length, mappedValue, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: PullParser.Failed): PullParser.Continuing<T> {
            return next.selected(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, previousLength + length, expected)
        }

        protected abstract fun map(length: Int, value: ValueProvider<INTERMEDIATE>): ValueProvider<OUT>
    }

    private class EndParseContinuation<IN, OUT> : ParseContinuation<IN, OUT> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: PullParser.Failed): PullParser.Matched<IN> {
            return PullParser.Matched(advance, EndPullParser, failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: PullParser.Failed): PullParser.Continuing<T> {
            return PullParser.Matched(advance, parser, failedChoices)
        }
    }

    private object EndPullParser : PullParser<Any?> {
        override fun toString(): String {
            return "{end}"
        }

        override fun stop(input: Any?): PullParser.Failed {
            return PullParser.Failed.None
        }

        override fun parse(input: Any?, max: Int): PullParser.Result<Any?> {
            return PullParser.Matched(0, this)
        }
    }
}
