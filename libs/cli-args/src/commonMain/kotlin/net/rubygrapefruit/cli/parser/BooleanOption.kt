package net.rubygrapefruit.cli.parser

internal class BooleanOption(private val flag: String, private val incorrectPrefix: String) : AbstractParser<Boolean>(), Option<Boolean> {
    override fun start(): ParseState<Boolean> {
        return State(flag, incorrectPrefix)
    }

    private class State(val flag: String, val incorrectPrefix: String) : ParseState<Boolean> {
        private var value = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            val first = args.first()
            if (first != flag) {
                return if (first == incorrectPrefix) {
                    ParseState.Failure(1, 0, UnexpectedOption(first, flag))
                } else {
                    ParseState.NotRecognized
                }
            }
            return if (value) {
                ParseState.Failure(1, 0, RepeatedOption(flag))
            } else {
                value = true
                ParseState.Matched(1)
            }
        }

        override fun finish(): ParseState.Result<Boolean> {
            return ParseState.FixedResult(value)
        }
    }
}