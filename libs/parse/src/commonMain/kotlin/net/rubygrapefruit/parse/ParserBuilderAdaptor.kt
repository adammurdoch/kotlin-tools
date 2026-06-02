package net.rubygrapefruit.parse

internal class ParserBuilderAdaptor<IN, OUT>(val parser: ParserBuilder<IN, OUT>) : CompiledParser<IN, OUT> {
    override fun start(next: ParseContinuation<IN, OUT>): PullParser<IN> {
        return parser.start(next)
    }
}