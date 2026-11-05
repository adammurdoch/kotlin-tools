package net.rubygrapefruit.cli.parser

internal class BooleanOption(private val flag: String, private val incorrectPrefix: String) : AbstractParser<Boolean>(), Option<Boolean> {
    override fun start(): ParseState<Boolean> {
        return State(flag, incorrectPrefix)
    }

    private class State(val flag: String, val incorrectPrefix: String) : ParseState<Boolean> {
        private var matchedIndex = -1

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            val first = args.first()
            return if (first != flag || matchedIndex >= 0) {
                ParseState.NotRecognized
            } else {
                matchedIndex = args.index
                ParseState.Matched(1)
            }
        }

        override fun claimUnrecognized(args: InputArgs): Problem? {
            return if (args.first() == incorrectPrefix) {
                UnexpectedOption(args.first(), flag)
            } else if (args.index > matchedIndex && args.first() == flag) {
                RepeatedOption(flag)
            } else {
                null
            }
        }

        override fun finish(): ParseState.Result<Boolean> {
            return ParseState.FixedResult(matchedIndex >= 0)
        }
    }
}