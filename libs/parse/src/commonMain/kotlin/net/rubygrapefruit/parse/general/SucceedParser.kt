package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*

internal class SucceedParser<OUT>(
    private val result: OUT
) : Parser<Any, OUT>, CombinatorBuilder<OUT>, DiscardableParser<Any> {
    override fun withNoResult(): Parser<Any, Unit> {
        return SucceedParser(Unit)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return SucceedCompiledParser(ValueProvider.of(result))
    }

    companion object {
        val NoResult = SucceedParser(Unit)
    }

    internal class SucceedCompiledParser<IN, OUT>(
        val result: ValueProvider<OUT>
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return SucceedPullParser(result, next)
        }
    }

    private class SucceedPullParser<IN, OUT, NEXT>(
        val result: ValueProvider<OUT>,
        val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        override fun toString(): String {
            return "{succeed}"
        }

        override fun stop(): PullParser.Failed {
            return next.next(0, result).stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return next.matched(0, 0, result)
        }
    }
}