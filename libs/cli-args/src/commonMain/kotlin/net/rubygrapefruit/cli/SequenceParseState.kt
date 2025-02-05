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
                    if (states.isNotEmpty()) {
                        ParseState.Continue(result.consumed, this, result.hint, result.apply)
                    } else {
                        result
                    }
                }

                is ParseState.Continue -> {
                    states[0] = result.state
                    ParseState.Continue(result.consumed, this, result.hint, result.apply)
                }

                is ParseState.Failure -> result

                is ParseState.Nothing -> result
            }
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return finish(states)
    }
}