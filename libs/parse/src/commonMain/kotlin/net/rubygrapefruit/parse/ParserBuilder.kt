package net.rubygrapefruit.parse

internal interface ParserBuilder<IN, OUT> {
    fun <NEXT> build(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>
}