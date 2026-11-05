package net.rubygrapefruit.cli.parser

internal class DefaultParameter(private val name: String) : AbstractParser<String>(), Parameter<String> {
    override fun start(): ParseState<String> {
        return State(name)
    }

    override fun usage(): UsageInternal {
        return DeleteMe
    }

    private class State(val name: String) : ParseState<String> {
        private var value: String? = null

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            return if (args.firstIsOption() || value != null) {
                ParseState.NotRecognized
            } else {
                value = args.first()
                ParseState.Matched(1)
            }
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