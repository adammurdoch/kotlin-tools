package net.rubygrapefruit.cli.parser

internal class DefaultParameter(private val name: String) : AbstractParser<String>(), Parameter<String> {
    override fun start(): ParseState<String> {
        return State(name)
    }

    private class State(val name: String) : ParseState<String> {
        private var value: String? = null

        override fun maybeParse(args: InputArgs): ParseState.Result {
            val first = args.first()
            if (first.startsWith("-") || value != null) {
                return ParseState.NotRecognized
            }
            value = first
            return ParseState.Continue(1)
        }

        override fun finish(): ParseState.FinalResult<String> {
            val value = value
            return if (value == null) {
                ParseState.FinalFailure(listOf("Parameter <$name> not provided."))
            } else {
                ParseState.Success(value)
            }
        }
    }
}