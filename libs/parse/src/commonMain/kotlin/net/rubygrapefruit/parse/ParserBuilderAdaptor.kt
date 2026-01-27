package net.rubygrapefruit.parse

internal class ParserBuilderAdaptor<IN, OUT>(val parser: ParserBuilder<IN, OUT>) : CompiledParser<IN, OUT> {
    override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return parser.start(next)
    }
}