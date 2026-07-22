package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal data object PositionParser : Parser<Any, Position>, CombinatorBuilder<Position>, DiscardableParser<Any> {
    override fun withNoResult(): Parser<Any, Unit> {
        return SucceedParser.NoResult
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Position> {
        @Suppress("UNCHECKED_CAST")
        return PositionCompiledParser as CompiledParser<IN, Position>
    }

    internal data object PositionCompiledParser : CompiledParser<Input<*>, Position> {
        override fun start(start: Position, next: ParseContinuation<Input<*>, Position>): PullParser<Input<*>> {
            return SucceedParser.SucceedPullParser(ValueProvider.of(start), next)
        }
    }
}