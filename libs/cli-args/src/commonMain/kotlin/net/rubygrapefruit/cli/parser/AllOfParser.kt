package net.rubygrapefruit.cli.parser

internal class AllOfParser<A, B, OUT>(
    private val a: ParserInternal<A>,
    private val b: ParserInternal<B>,
    private val map: (A, B) -> OUT
) : AbstractParser<OUT>() {
    override fun start(): ParseState<OUT> {
        return State(a.start(), b.start(), map)
    }

    private class State<A, B, OUT>(val a: ParseState<A>, val b: ParseState<B>, val map: (A, B) -> OUT) : ParseState<OUT> {
        override fun maybeParse(args: InputArgs): ParseState.Result {
            val aResult = a.maybeParse(args)
            if (aResult !is ParseState.NotRecognized) {
                return aResult
            }
            return b.maybeParse(args)
        }

        override fun finish(): Parser.Result<OUT> {
            val aResult = a.finish()
            val bResult = b.finish()
            return zipping(aResult, bResult, map)
        }
    }
}