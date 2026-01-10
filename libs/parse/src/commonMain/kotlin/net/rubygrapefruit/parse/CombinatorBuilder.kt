package net.rubygrapefruit.parse

internal interface CombinatorBuilder<OUT> {
    fun <IN : Input<*>, NEXT> build(converter: Converter<IN>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>

    interface Converter<IN> {
        fun <OUT> builder(parser: Parser<*, OUT>): ParserBuilder<IN, OUT>

        fun <OUT> convert(parser: Parser<*, OUT>): PullParser<IN, OUT>

        fun <OUT, NEXT> convert(parser: Parser<*, OUT>, next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT>

        fun <OUT, NEXT> convert(parser: Parser<*, OUT>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>
    }
}
