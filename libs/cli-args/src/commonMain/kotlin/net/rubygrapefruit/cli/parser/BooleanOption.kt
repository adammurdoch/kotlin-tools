package net.rubygrapefruit.cli.parser

internal class BooleanOption(val flag: String) : AbstractParser<Boolean>(), Option<Boolean> {
    override fun start(): ParseState<Boolean> {
        return State(flag)
    }

    private class State(val flag: String) : ParseState<Boolean> {
        private var value = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            return if (args.first() == flag) {
                value = true
                ParseState.Continue(1)
            } else {
                ParseState.NotRecognized
            }
        }

        override fun finish(): ParseState.Result<Boolean> {
            return ParseState.FixedResult(value)
        }
    }
}