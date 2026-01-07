package net.rubygrapefruit.parse

internal interface ParserBuilder<OUT> {
    fun <IN: Input<*>> build(converter: Converter<IN>): PullParser<IN, OUT>

    interface Converter<in IN> {
        fun <OUT> convert(parser: Parser<*, OUT>): PullParser<IN, OUT>
    }
}
