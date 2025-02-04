package net.rubygrapefruit.cli

internal class SequenceParseState(initialStates: List<ParseState>) : ParseState {
    private val states = initialStates.toMutableList()
    private val results = mutableListOf<() -> Unit>()

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val state = states.firstOrNull()
        if (state == null) {
            return ParseState.Nothing
        } else {
            val result = state.parseNextValue(args)
            return when (result) {
                is ParseState.Success -> {
                    states.removeFirst()
                    results.add(result.apply)
                    if (states.isNotEmpty()) {
                        ParseState.Continue(result.consumed, this)
                    } else {
                        ParseState.Success(result.consumed, collectActions())
                    }
                }

                is ParseState.Continue -> {
                    states[0] = result.state
                    ParseState.Continue(result.consumed, this)
                }

                is ParseState.Failure, ParseState.Nothing -> {
                    result
                }
            }
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        while (states.isNotEmpty()) {
            val state = states.removeFirst()
            val result = state.endOfInput()
            when (result) {
                is ParseState.FinishSuccess -> results.add(result.apply)
                is ParseState.FinishFailure -> return result
            }
        }
        return ParseState.FinishSuccess(collectActions())
    }

    private fun collectActions(): () -> Unit {
        val actions = results.toList()
        return {
            for (action in actions) {
                action()
            }
        }
    }
}