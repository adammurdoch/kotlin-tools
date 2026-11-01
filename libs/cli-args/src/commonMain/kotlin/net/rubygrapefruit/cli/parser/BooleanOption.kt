package net.rubygrapefruit.cli.parser

internal class BooleanOption(val flag: String) : AbstractParser<Boolean>(), Option<Boolean> {
    override fun start(): ParseState<Boolean> {
        return State(flag)
    }

    private class State(val flag: String) : ParseState<Boolean> {
        private var value = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            val first = args.first()
            if (first != flag) {
                return ParseState.NotRecognized
            }
            return if (value) {
                ParseState.Failure(1, 0, Message.Text("Option $flag can only appear once"))
            } else {
                value = true
                ParseState.Continue(1)
            }
        }

        override fun finish(): ParseState.Result<Boolean> {
            return ParseState.FixedResult(value)
        }
    }
}