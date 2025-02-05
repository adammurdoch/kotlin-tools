package net.rubygrapefruit.cli

import net.rubygrapefruit.cli.DefaultActionParameter.NameUsage

internal class ActionParameterParseState<T : Action>(
    private val target: DefaultActionParameter<T>,
    private val context: ParseContext,
    private val actions: ActionSet<T>,
    private val host: Host
) : ParseState {
    override fun parseNextValue(args: List<String>): ParseState.Result {
        val name = args.first()
        val action = actions.lookup(name)
        if (action != null) {
            val nestedContext = context.nested(target, listOf(NameUsage(name)) + action.second.value.positional())
            return ParseState.Continue(1, stateFor(action.first, action.second.value, nestedContext))
        }

        if (host.isOption(name)) {
            return ParseState.Nothing
        }

        if (actions.default != null) {
            val nestedContext = context.nested(target, actions.default.value.positional())
            return ParseState.Continue(0, stateFor(null, actions.default.value, nestedContext))
        }

        return ParseState.Failure(1, "Unknown action: $name", positional = context.positional, actions = target.actionInfo)
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (actions.default != null) {
            val nestedContext = context.nested(target, actions.default.value.positional())
            stateFor(null, actions.default.value, nestedContext).endOfInput()
        } else {
            ParseState.FinishFailure("Action not provided", resolution = "Please specify an action to run.", positional = context.positional, actions = target.actionInfo)
        }
    }

    private fun stateFor(display: String?, action: T, nestedContext: ParseContext): ParseState {
        val state = action.state(nestedContext) {
            target.value(action)
        }
        return if (display != null) {
            HintApplyingParseState(state, display, actions)
        } else {
            state
        }
    }

    private class HintApplyingParseState(private var state: ParseState, private val selected: String, private val actions: ActionSet<*>) : ParseState, FailureHint {
        override fun parseNextValue(args: List<String>): ParseState.Result {
            val result = state.parseNextValue(args)
            return when (result) {
                is ParseState.Success -> result.withHint(this)
                is ParseState.Failure -> result.withHint(this)

                is ParseState.Continue -> {
                    state = result.state
                    ParseState.Continue(result.consumed, this)
                }

                is ParseState.Nothing -> result
            }
        }

        override fun endOfInput(): ParseState.FinishResult {
            return state.endOfInput()
        }

        override fun map(args: List<String>): ParseState.Failure? {
            val name = args.first()
            val action = actions.lookup(name)
            return if (action != null) {
                if (action.first == selected) {
                    ParseState.Failure(1, "Cannot use $selected multiple times")
                } else {
                    ParseState.Failure(1, "Cannot use ${action.first} with $selected")
                }
            } else {
                null
            }
        }
    }
}