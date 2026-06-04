package net.rubygrapefruit.parse

/**
 * Implementations are cached and reused and so must be stateless.
 */
internal interface CompiledParser<IN, out OUT> {
    /**
     * Starts parsing.
     */
    fun start(next: ParseContinuation<IN, OUT>): PullParser<IN>

    /**
     * A convenience for [start].
     */
    fun then(next: (length: Int, value: ValueProvider<OUT>) -> PullParser<IN>): PullParser<IN> {
        return start(ParseContinuation.prefix(next))
    }

    /**
     * Starts this parser as the last parser.
     */
    fun start(): PullParser<IN> {
        return start(ParseContinuation.end())
    }
}