package net.rubygrapefruit.parse

internal interface CompiledParser<IN, OUT> {
    fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>

    fun start(): PullParser<IN, OUT> {
        return start(ParseContinuation.of())
    }
}