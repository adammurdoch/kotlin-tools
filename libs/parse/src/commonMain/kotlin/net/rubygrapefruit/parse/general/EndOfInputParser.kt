package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*

internal class EndOfInputParser<OUT>(val result: OUT) : Parser<Any, OUT>, CombinatorBuilder<OUT>, DiscardableParser<Any> {
    override fun withNoResult(): Parser<Any, Unit> {
        return EndOfInputParser(Unit)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return EndOfInputCompiledParser(result)
    }

    internal class EndOfInputCompiledParser<IN : Input<*>, OUT>(val result: OUT) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return EndOfInputPullParser(result, next)
        }
    }

    private class EndOfInputPullParser<IN : Input<*>, OUT, NEXT>(val result: OUT, val next: ParseContinuation<IN, OUT, NEXT>) : PullParser<IN, NEXT> {
        private val expectation: Expectation = Expectation.One("end of input")

        override fun toString(): String {
            return "{end-of-input}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed(0, expectation)
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return if (input.available > 0) {
                PullParser.Failed(0, expectation)
            } else if (input.finished) {
                next.matched(0, 0, result)
            } else {
                PullParser.RequireMore(0, 0, false, this)
            }
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