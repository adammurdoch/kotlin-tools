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
            return ParseState.Continue(
                1,
                stateFor(action.second.value, nestedContext),
                ActionHint(action.first, actions),
            ) {
                target.value(action.second.value)
            }
        }

        if (actions.default != null) {
            val nestedContext = context.nested(target, actions.default.value.positional())
            return ParseState.Continue(0, stateFor(actions.default.value, nestedContext), null) {
                target.value(actions.default.value)
            }
        }

        if (host.isOption(name)) {
            return ParseState.Nothing
        }

        return ParseState.Failure(1, "Unknown action: $name", positional = context.positional, actions = target.actionInfo)
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (actions.default != null) {
            val nestedContext = context.nested(target, actions.default.value.positional())
            stateFor(actions.default.value, nestedContext).endOfInput()
        } else {
            ParseState.FinishFailure("Action not provided", resolution = "Please specify an action to run.", positional = context.positional, actions = target.actionInfo)
        }
    }

    private fun stateFor(action: T, nestedContext: ParseContext): ParseState {
        return action.state(nestedContext)
    }

    private class ActionHint(private val selected: String, private val actions: ActionSet<*>) : FailureHint {
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