package net.rubygrapefruit.parse

internal class TracingParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>, NEXT> build(converter: CombinatorBuilder.Converter<IN>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        val parser = converter.convert(parser, next)
        return TracingPullParser(parser)
    }

    private class TracingPullParser<IN, OUT>(private val parser: PullParser<IN, OUT>) : PullParser<IN, OUT> {
        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            val result = parser.parse(input, max)
            println("-> $parser (max=$max) => $result")
            return when (result) {
                is PullParser.Finished -> {
                    result
                }

                is PullParser.RequireMore -> {
                    PullParser.RequireMore(result.advance, TracingPullParser(result.parser))
                }
            }
        }
    }
}