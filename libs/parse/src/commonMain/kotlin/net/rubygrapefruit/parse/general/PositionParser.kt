package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal data object PositionParser : Parser<Any, Position>, CombinatorBuilder<Position> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Position> {
        return PositionCompiledParser as CompiledParser<IN, Position>
    }

    private data object PositionCompiledParser : CompiledParser<Input<*>, Position> {
        override fun start(start: Position, next: ParseContinuation<Input<*>, Position>): PullParser<Input<*>> {
            return SucceedParser.SucceedPullParser(ValueProvider.of(start), next)
        }
    }
}