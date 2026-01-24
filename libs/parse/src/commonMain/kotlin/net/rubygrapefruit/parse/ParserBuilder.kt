package net.rubygrapefruit.parse

/**
 * A simplified variant of [CombinatorBuilder] that has no compilation step.
 * Parsers must always match at least one value.
 */
internal interface ParserBuilder<IN, OUT> {
    /**
     * What does this parser expect at the start of input?
     */
    val expectation: Expectation

    /**
     * Creates a copy of this parser that produces no result.
     */
    fun withNoResult(): ParserBuilder<IN, Unit>

    /**
     * Starts parsing.
     */
    fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>
}