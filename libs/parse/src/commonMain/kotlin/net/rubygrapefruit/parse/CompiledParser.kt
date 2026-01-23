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

    fun <NEXT> start(expectation: Expectation, next: (length: Int, value: OUT) -> PullParser<IN, NEXT>): PullParser<IN, NEXT> {
        return start(ParseContinuation.of(expectation, next))
    }

    /**
     * Starts this parser as the last parser.
     */
    fun start(): PullParser<IN, OUT> {
        return start(ParseContinuation.end())
    }
}