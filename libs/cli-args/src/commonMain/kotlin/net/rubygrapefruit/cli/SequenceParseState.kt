package net.rubygrapefruit.cli

internal class SequenceParseState(initialStates: List<ParseState>) : AbstractCollectingParseState() {
    private val states = initialStates.toMutableList()

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val state = states.firstOrNull()
        if (state == null) {
            return ParseState.Nothing
        } else {
            val result = state.parseNextValue(args)
            return when (result) {
                is ParseState.Success -> {
                    states.removeFirst()
                    collect(result)
                    if (states.isNotEmpty()) {
                        ParseState.Continue(result.consumed, this)
                    } else {
                        ParseState.Success(result.consumed, collectHints(), collectActions())
                    }
                }

                is ParseState.Continue -> {
                    states[0] = result.state
                    ParseState.Continue(result.consumed, this)
                }

                is ParseState.Failure -> {
                    collect(result)
                    result.withHint(collectHints())
                }

                is ParseState.Nothing -> result
            }
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return finish(states)
    }
}