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
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return SucceedPullParser(result, next)
        }
    }

    private class SucceedPullParser<IN, OUT>(
        val result: ValueProvider<OUT>,
        val next: ParseContinuation<IN, OUT>
    ) : PullParser<IN> {
        override fun toString(): String {
            return "{succeed}"
        }

        override fun stop(): PullParser.Failed {
            return next.matched(0, 0, result).parser.stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            return next.matched(0, 0, result)
        }
    }
}