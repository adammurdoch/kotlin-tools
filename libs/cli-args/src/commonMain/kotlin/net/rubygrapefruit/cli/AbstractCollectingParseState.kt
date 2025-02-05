package net.rubygrapefruit.cli

internal abstract class AbstractCollectingParseState : ParseState {
    protected fun finish(states: List<ParseState>): ParseState.FinishResult {
        val pendingActions = mutableListOf<() -> Unit>()
        for (state in states) {
            val result = state.endOfInput()
            when (result) {
                is ParseState.FinishSuccess -> pendingActions.add(result.apply)
                is ParseState.FinishFailure -> return result
            }
        }
        return ParseState.FinishSuccess {
            for (action in pendingActions) {
                action()
            }
        }
    }
}