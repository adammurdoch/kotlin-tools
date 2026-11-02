package net.rubygrapefruit.cli.parser

internal class DefaultParameter(private val name: String) : AbstractParser<String>(), Parameter<String> {
    override fun start(): ParseState<String> {
        return State(name)
    }

    private class State(val name: String) : ParseState<String> {
        private var value: String? = null

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            if (args.firstIsOption() || value != null) {
                return ParseState.NotRecognized
            }
            value = args.first()
            return ParseState.Continue(1)
        }

        override fun finish(): ParseState.Result<String> {
            val value = value
            return if (value == null) {
                ParseState.FinalFailure(listOf(MissingParameter(name)))
            } else {
                ParseState.FixedResult(value)
            }
        }
    }
}