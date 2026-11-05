package net.rubygrapefruit.cli.parser

internal class BooleanOption(private val flag: String, private val incorrectPrefix: String) : AbstractParser<Boolean>(), Option<Boolean> {
    override fun start(): ParseState<Boolean> {
        return State(flag, incorrectPrefix)
    }

    private class State(val flag: String, val incorrectPrefix: String) : ParseState<Boolean> {
        private var matched = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            val first = args.first()
            return if (first != flag || matched) {
                ParseState.NotRecognized
            } else {
                matched = true
                ParseState.Matched(1)
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            return if (args.first() == incorrectPrefix) {
                ParseState.Failure(1, 0, UnexpectedOption(args.first(), flag))
            } else if (args.first() == flag) {
                ParseState.Failure(1, 0, RepeatedOption(flag))
            } else {
                null
            }
        }

        override fun finish(): ParseState.Result<Boolean> {
            return ParseState.FixedResult(matched)
        }
    }
}