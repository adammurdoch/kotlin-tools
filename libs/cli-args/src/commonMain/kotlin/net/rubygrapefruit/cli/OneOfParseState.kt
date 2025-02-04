package net.rubygrapefruit.cli

internal class OneOfParseState(initialStates: List<ParseState>) : ParseState {
    private val states = initialStates.toMutableList()
    private val results = mutableListOf<() -> Unit>()

    override fun parseNextValue(args: List<String>): ParseState.Result {
        var index = 0
        while (index < states.size) {
            val state = states[index]
            val result = state.parseNextValue(args)
            when (result) {
                is ParseState.Success -> {
                    results.add(result.apply)
                    states.removeAt(index)
                    return if (states.isNotEmpty()) {
                        ParseState.Continue(result.consumed, this)
                    } else {
                        ParseState.Success(result.consumed, collectActions())
                    }
                }

                is ParseState.Continue -> {
                    states[index] = result.state
                    return ParseState.Continue(result.consumed, this)
                }

                is ParseState.Failure -> return result
                is ParseState.Nothing -> index++
            }
        }
        return ParseState.Nothing
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