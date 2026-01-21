package net.rubygrapefruit.parse

/**
 * Implementations are cached and reused and so must be stateless.
 */
internal interface CompiledParser<IN, out OUT> {
    /**
     * Does this parser advance zero input items on match?
     */
    val mayNotAdvanceOnMatch: Boolean

    /**
     * What does this parser expect at the start of input?
     */
    val expectation: Expectation

    /**
     * Starts parsing.
     */
    fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>

    fun <NEXT> start(next: (PullParser.Matched<OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT> {
        return start(ParseContinuation.of(next))
    }

    fun start(): PullParser<IN, OUT> {
        return start(ParseContinuation.of())
    }
}