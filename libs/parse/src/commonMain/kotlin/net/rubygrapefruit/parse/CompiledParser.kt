package net.rubygrapefruit.parse

/**
 * Implementations are cached and reused and so must be stateless.
 */
internal interface CompiledParser<IN, out OUT> {
    /**
     * Starts parsing.
     */
    fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>

    /**
     * A convenience for [start].
     */
    fun <NEXT> start(next: (length: Int, value: OUT) -> PullParser<IN, NEXT>): PullParser<IN, NEXT> {
        return start(ParseContinuation.of(next))
    }

    /**
     * Starts this parser as the last parser.
     */
    fun start(): PullParser<IN, OUT> {
        return start(ParseContinuation.end())
    }
}