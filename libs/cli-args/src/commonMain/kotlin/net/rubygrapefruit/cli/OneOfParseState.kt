package net.rubygrapefruit.cli

internal class OneOfParseState(initialStates: List<ParseState>) : AbstractCollectingParseState() {
    private val states = initialStates.toMutableList()

    override fun parseNextValue(args: List<String>): ParseState.Result {
        var index = 0
        while (index < states.size) {
            val state = states[index]
            val result = state.parseNextValue(args)
            when (result) {
                is ParseState.Success -> {
                    collect(result)
                    states.removeAt(index)
                    return if (states.isNotEmpty()) {
                        ParseState.Continue(result.consumed, this)
                    } else {
                        ParseState.Success(result.consumed, collectHints(), collectActions())
                    }
                }

                is ParseState.Continue -> {
                    states[index] = result.state
                    return ParseState.Continue(result.consumed, this)
                }

                is ParseState.Failure -> {
                    collect(result)
                    return result.withHint(collectHints())
                }

                is ParseState.Nothing -> index++
            }
        }
        return ParseState.Nothing
    }

    override fun endOfInput(): ParseState.FinishResult {
        return finish(states)
    }
}