package net.rubygrapefruit.parse

internal interface ParserBuilder<OUT> {
    fun <IN: Input<*>> build(converter: Converter<IN, OUT>): PullParser<IN, OUT>

    interface Converter<in IN, OUT> {
        fun convert(parser: Parser<*, OUT>): PullParser<IN, OUT>
    }
}
