package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class EndOfInputParser<OUT>(val result: OUT) : Parser<Any, OUT>, CombinatorBuilder<OUT>, DiscardableParser<Any> {
    override fun withNoResult(): Parser<Any, Unit> {
        return EndOfInputParser(Unit)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return EndOfInputCompiledParser(result)
    }

    internal class EndOfInputCompiledParser<IN : Input<*>, OUT>(val result: OUT) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return EndOfInputPullParser(result, next)
        }
    }

    private class EndOfInputPullParser<IN : Input<*>, OUT>(val result: OUT, val next: ParseContinuation<IN, OUT>) : PullParser<IN> {
        private val expectation: Expectation = Expectation.One("end of input")

        override fun toString(): String {
            return "{end-of-input}"
        }

        override fun stop(input: IN): PullParser.Failed {
            return stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            return if (input.available > 0) {
                stop()
            } else if (input.finished) {
                next.matched(input, 0, 0, result)
            } else {
                PullParser.RequireMore(0, this)
            }
        }

        private fun stop(): PullParser.Failed {
            return next.failed(0, 0, expectation)
        }
    }
}

/**
 * Returns a parser that matches the end of input. Does not consume any input or produce a result
 */
fun endOfInput(): Parser<Any, Unit> {
    return EndOfInputParser(Unit)
}

/**
 * Returns a parser that matches the end of input and produces the given result. Does not consume any input.
 */
fun <OUT> endOfInput(result: OUT): Parser<Any, OUT> {
    return EndOfInputParser(result)
}