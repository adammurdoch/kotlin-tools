package net.rubygrapefruit.parse

internal interface CombinatorBuilder<OUT> {
    fun <IN : Input<*>> compile(converter: Converter<IN>): CompiledParser<IN, OUT>

    interface Converter<IN> {
        fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT>

        fun <OUT, NEXT> convert(parser: Parser<*, OUT>, next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT>

        fun <OUT, NEXT> convert(parser: Parser<*, OUT>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT>
    }
}
