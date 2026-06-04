package net.rubygrapefruit.parse

/**
 * Implementations are cached and reused and so must be stateless.
 */
internal interface CompiledParser<IN, out OUT> {
    /**
     * Starts parsing.
     */
    fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN>

    /**
     * A convenience for [start].
     */
    fun then(start: Position, next: (length: Int, value: ValueProvider<OUT>) -> PullParser<IN>): PullParser<IN> {
        return start(start, ParseContinuation.prefix(next))
    }
}