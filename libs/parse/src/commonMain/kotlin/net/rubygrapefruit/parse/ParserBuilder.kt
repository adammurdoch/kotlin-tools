package net.rubygrapefruit.parse

/**
 * A simplified variant of [CombinatorBuilder]. Parsers must always match at least one value.
 */
internal interface ParserBuilder<IN, OUT> {
    fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>
}