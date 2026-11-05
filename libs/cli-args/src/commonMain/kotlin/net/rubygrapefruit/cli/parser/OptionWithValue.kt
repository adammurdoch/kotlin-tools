package net.rubygrapefruit.cli.parser

internal class OptionWithValue(name: String) : AbstractParser<String?>(), StringOption {
    private val flag = if (name.length == 1) "-$name" else "--$name"
    private val incorrectPrefix = if (name.length == 1) "--$name" else "-$name"

    override fun boolean(): Option<Boolean> {
        return BooleanOption(flag, incorrectPrefix)
    }

    override fun start(): ParseState<String?> {
        return State(flag, incorrectPrefix)
    }

    private class State(private val flag: String, private val incorrectPrefix: String) : ParseState<String?> {
        private var value: String? = null
        private var seen = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            val first = args.first()
            if (first != flag) {
                return if (first == incorrectPrefix) {
                    val count = if (args.length >= 2 && !args.secondIsOption()) 2 else 1
                    ParseState.Failure(count, 0, UnexpectedOption(first, flag))
                } else {
                    ParseState.NotRecognized
                }
            }
            if (seen) {
                val count = if (args.length >= 2 && !args.secondIsOption()) 2 else 1
                return ParseState.Failure(count, 0, RepeatedOption(flag))
            }
            seen = true
            return if (args.length == 1 || args.length >= 2 && args.secondIsOption()) {
                ParseState.Failure(1, 1, MissingOptionValue(flag))
            } else {
                value = args.second()
                ParseState.Matched(2)
            }
        }

        override fun finish(): ParseState.Result<String?> {
            return ParseState.FixedResult(value)
        }
    }
}