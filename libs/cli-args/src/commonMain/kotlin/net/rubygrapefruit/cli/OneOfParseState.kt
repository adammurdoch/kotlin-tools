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
                    states.removeAt(index)
                    return if (states.isNotEmpty()) {
                        ParseState.Continue(result.consumed, this, result.hint, result.apply)
                    } else {
                        result
                    }
                }

                is ParseState.Continue -> {
                    states[index] = result.state
                    return ParseState.Continue(result.consumed, this, result.hint, result.apply)
                }

                is ParseState.Failure -> {
                    return result
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