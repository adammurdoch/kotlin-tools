package net.rubygrapefruit.parse

internal interface ParserBuilder<IN, OUT> {
    fun <NEXT> build(next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT>
}