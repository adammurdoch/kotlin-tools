package net.rubygrapefruit.cli.parser

internal class DefaultOption(name: String) : AbstractParser<String?>(), Option<String?> {
    private val flag = if (name.length == 1) "-$name" else "--$name"
    private val lenient = if (name.length == 1) "--$name" else "-$name"

    override fun start(): ParseState<String?> {
        return State(flag, lenient)
    }

    private class State(private val flag: String, private val lenient: String) : ParseState<String?> {
        private var value: String? = null
        private var seen = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            val first = args.first()
            if (first != flag) {
                return if (first == lenient) {
                    val count = if (args.length >= 2 && !args.secondIsOption()) 2 else 1
                    ParseState.Failure(count, 0, "Option $flag should be used instead of '$first'")
                } else {
                    ParseState.NotRecognized
                }
            }
            if (seen) {
                val count = if (args.length >= 2 && !args.secondIsOption()) 2 else 1
                return ParseState.Failure(count, 0, "Option $first can only appear once")
            }
            seen = true
            return if (args.length == 1 || args.length >= 2 && args.secondIsOption()) {
                ParseState.Failure(1, 1, "No value provided for option $first")
            } else {
                value = args.second()
                ParseState.Continue(2)
            }
        }

        override fun finish(): ParseState.Result<String?> {
            return ParseState.FixedResult(value)
        }
    }
}