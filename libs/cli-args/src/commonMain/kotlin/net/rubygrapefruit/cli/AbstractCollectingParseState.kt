package net.rubygrapefruit.cli

internal abstract class AbstractCollectingParseState : ParseState {
    private val pendingActions = mutableListOf<() -> Unit>()
    private val pendingHints = mutableListOf<FailureHint>()

    protected fun collect(action: () -> Unit) {
        pendingActions.add(action)
    }

    protected fun collect(result: ParseState.Success) {
        pendingActions.add(result.apply)
        if (result.hint != null) {
            pendingHints.add(result.hint)
        }
    }

    protected fun collect(result: ParseState.Failure) {
        if (result.hint != null) {
            pendingHints.add(result.hint)
        }
    }

    protected fun collectHints(): FailureHint? {
        return when (pendingHints.size) {
            0 -> null
            1 -> pendingHints[0]
            else -> {
                val hints = pendingHints.toList()
                object : FailureHint {
                    override fun map(args: List<String>): ParseState.Failure? {
                        for (hint in hints) {
                            val failure = hint.map(args)
                            if (failure != null) {
                                return failure
                            }
                        }
                        return null
                    }
                }
            }
        }
    }

    protected fun collectActions(): () -> Unit {
        val actions = pendingActions.toList()
        return {
            for (action in actions) {
                action()
            }
        }
    }

    protected fun finish(states: List<ParseState>): ParseState.FinishResult {
        for (state in states) {
            val result = state.endOfInput()
            when (result) {
                is ParseState.FinishSuccess -> pendingActions.add(result.apply)
                is ParseState.FinishFailure -> return result
            }
        }
        return ParseState.FinishSuccess(collectActions())
    }
}