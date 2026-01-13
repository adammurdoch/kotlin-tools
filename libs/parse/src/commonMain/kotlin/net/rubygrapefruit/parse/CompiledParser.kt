package net.rubygrapefruit.parse

internal interface CompiledParser<IN, OUT> {
    fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>

    fun <NEXT> start(next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT> {
        return start(ParseContinuation.of(next))
    }

    fun start(): PullParser<IN, OUT> {
        return start(ParseContinuation.of())
    }
}