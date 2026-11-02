package net.rubygrapefruit.cli.parser

internal class DefaultLiteral(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {
    override fun start(): ParseState<Unit> {
        return State(text)
    }

    private class State(val text: String) : ParseState<Unit> {
        private var seen = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            if (!seen && args.first() == text) {
                seen = true
                return ParseState.Continue(1)
            }
            return ParseState.NotRecognized
        }

        override fun finish(): ParseState.Result<Unit> {
            return if (seen) {
                ParseState.FixedResult(Unit)
            } else {
                ParseState.FinalFailure(listOf(MissingLiteral(text)))
            }
        }
    }
}
