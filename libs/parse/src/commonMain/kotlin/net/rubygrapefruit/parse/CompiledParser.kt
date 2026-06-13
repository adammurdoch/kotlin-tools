package net.rubygrapefruit.parse

/**
 * Implementations are cached and reused and so must be stateless.
 */
internal interface CompiledParser<IN, out OUT> {
    /**
     * Starts parsing.
     */
    fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN>
}