package net.rubygrapefruit.cli.parser

internal class DefaultLiteral(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {
    override fun start(): ParseState<Unit> {
        return State(text)
    }

    private class State(val text: String) : ParseState<Unit> {
        private var seen = false
        private var failed = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            if (seen || args.firstIsOption()) {
                return ParseState.NotRecognized
            }
            seen = true
            return if (args.first() == text) {
                ParseState.Continue(1)
            } else {
                failed = true
                ParseState.Failure(1, 0, UnexpectedArgument(args.first(), text))
            }
        }

        override fun finish(): ParseState.Result<Unit> {
            return if (seen) {
                ParseState.FixedResult(Unit)
            } else if (failed) {
                ParseState.FinalFailure(emptyList())
            } else {
                ParseState.FinalFailure(listOf(MissingLiteral(text)))
            }
        }
    }
}
