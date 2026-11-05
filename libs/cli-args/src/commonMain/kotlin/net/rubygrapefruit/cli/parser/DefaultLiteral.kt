package net.rubygrapefruit.cli.parser

internal class DefaultLiteral(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {
    override fun start(): ParseState<Unit> {
        return State(text)
    }

    private class State(val text: String) : ParseState<Unit> {
        private var claimed = false
        private var matched = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            if (matched || args.firstIsOption()) {
                return ParseState.NotRecognized
            }
            return if (args.first() == text) {
                matched = true
                ParseState.Matched(1)
            } else {
                ParseState.NotRecognized
            }
        }

        override fun claimUnrecognized(args: InputArgs): Problem? {
            return if (!matched && !claimed && !args.firstIsOption()) {
                claimed = true
                UnexpectedArgument(args.first(), text)
            } else {
                null
            }
        }

        override fun finish(): ParseState.Result<Unit> {
            return if (matched) {
                ParseState.FixedResult(Unit)
            } else if (claimed) {
                ParseState.FinalFailure(emptyList())
            } else {
                ParseState.FinalFailure(listOf(MissingLiteral(text)))
            }
        }
    }
}
