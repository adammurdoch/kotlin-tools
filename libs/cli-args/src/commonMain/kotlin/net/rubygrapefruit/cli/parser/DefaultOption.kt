package net.rubygrapefruit.cli.parser

internal class DefaultOption(name: String) : AbstractParser<String?>(), Option<String?> {
    private val flag = "-$name"

    override fun start(): ParseState<String?> {
        return State(flag)
    }

    private class State(private val flag: String) : ParseState<String?> {
        private var value: String? = null
        private var seen = false

        override fun maybeParse(args: InputArgs): ParseState.Result {
            val first = args.first()
            if (first != flag) {
                return ParseState.NotRecognized
            }
            if (seen) {
                val count = if (args.length >= 2 && !args.second().startsWith("-")) 2 else 1
                return ParseState.Failure(count, 0, "Option $first can only appear once")
            }
            seen = true
            return if (args.length == 1 || args.length >= 2 && args.second().startsWith("-")) {
                ParseState.Failure(1, 1, "No value provided for option $first")
            } else {
                value = args.second()
                ParseState.Continue(2)
            }
        }

        override fun finish(): ParseState.FinalResult<String?> {
            return ParseState.Success(value)
        }
    }
}