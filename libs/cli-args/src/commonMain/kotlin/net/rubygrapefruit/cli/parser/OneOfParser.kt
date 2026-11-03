package net.rubygrapefruit.cli.parser

internal class OneOfParser<OUT>(private val choices: List<ParserInternal<OUT>>) : AbstractParser<OUT>(), Parser<OUT> {
    override fun start(): ParseState<OUT> {
        return State(choices.map { ChoiceState(it.start()) })
    }

    private class State<OUT>(val choices: List<ChoiceState<OUT>>) : ParseState<OUT> {
        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            for (choice in choices) {
                val result = choice.state.maybeParse(args)
                if (result !is ParseState.Continue) {
                    choice.failed = true
                }
            }
            return ParseState.Continue(2)
        }

        override fun finish(): ParseState.Result<OUT> {
            for (choice in choices) {
                if (choice.failed) {
                    continue
                }
                val result = choice.state.finish()
                if (result is ParseState.Success) {
                    return result
                }
            }
            TODO("Not yet implemented")
        }
    }

    private class ChoiceState<OUT>(val state: ParseState<OUT>) {
        var failed = false
    }
}