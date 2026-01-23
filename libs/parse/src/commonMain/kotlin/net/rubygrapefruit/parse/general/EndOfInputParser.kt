package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*

internal class EndOfInputParser<IN> : Parser<IN, Unit>, CombinatorBuilder<Unit> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return EndOfInputCompiledParser()
    }

    private class EndOfInputCompiledParser<IN : Input<*>> : CompiledParser<IN, Unit> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation = Expectation.One("end of input")

        override fun <NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return EndOfInputPullParser(next)
        }
    }

    private class EndOfInputPullParser<IN : Input<*>, NEXT>(val next: ParseContinuation<IN, Unit, NEXT>) : PullParser<IN, NEXT> {
        override val expectation: Expectation = Expectation.One("end of input")

        override fun toString(): String {
            return "{end-of-input}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return if (input.available > 0) {
                PullParser.Failed(0, expectation)
            } else if (input.finished) {
                next.matched(0, 0, Unit)
            } else {
                PullParser.RequireMore(0, this)
            }
        }
    }
}

fun <IN> endOfInput(): Parser<IN, Unit> {
    return EndOfInputParser()
}