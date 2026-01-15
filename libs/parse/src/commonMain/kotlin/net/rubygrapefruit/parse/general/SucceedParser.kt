package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.ParseContinuation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.PullParser

internal class SucceedParser<IN, OUT>(private val result: OUT) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return SucceedCompiledParser(result)
    }

    companion object {
        fun <IN> of(): Parser<IN, Unit> {
            return SucceedParser(Unit)
        }

        fun <IN, OUT> compiled(result: OUT): CompiledParser<IN, OUT> {
            return SucceedCompiledParser(result)
        }

        fun <IN, NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
            return start(Unit, next)
        }

        fun <IN, OUT, NEXT> start(result: OUT, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            val result = next.matched(0, 0, result)
            return if (result is PullParser.RequireMore) {
                result.parser
            } else {
                FinishedPullParser(result)
            }
        }
    }

    private class SucceedCompiledParser<IN, OUT>(
        val result: OUT
    ) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return start(result, next)
        }
    }

    private class FinishedPullParser<IN, OUT>(
        private val result: PullParser.Result<IN, OUT>
    ) : PullParser<IN, OUT> {
        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun toString(): String {
            return "{finished result=$result}"
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            return result
        }
    }
}