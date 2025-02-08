package net.rubygrapefruit.cli

import net.rubygrapefruit.cli.DefaultActionParameter.NameUsage

internal class ActionParameterParseState<T : Action>(
    private val target: DefaultActionParameter<T>,
    private val context: ParseContext,
    private val actions: ActionSet<T>
) : ParseState {
    override fun parseNextValue(args: List<String>): ParseState.Result {
        val name = args.first()
        val selected = actions.lookup(name)
        if (selected != null) {
            val action = selected.second.value
            val nestedContext = action.nestedContext(context, target, listOf(NameUsage(name)))
            return ParseState.Continue(1, action.state(nestedContext), ActionHint(selected.first, actions)) {
                target.value(action)
            }
        }

        if (actions.default != null) {
            val action = actions.default.value
            val nestedContext = action.nestedContext(context, target, emptyList())
            return ParseState.Continue(0, action.state(nestedContext), null) {
                target.value(action)
            }
        }

        if (context.isOption(name)) {
            return ParseState.Nothing
        }

        return ParseState.Failure(1, "Unknown action: $name", positional = context.positional, actions = target.actionInfo)
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (actions.default != null) {
            val action = actions.default.value
            val nestedContext = action.nestedContext(context, target, emptyList())
            val result = action.state(nestedContext).endOfInput()
            return if (result is ParseState.FinishSuccess) {
                ParseState.FinishSuccess {
                    result.apply()
                    target.value(action)
                }
            } else {
                result
            }
        } else {
            ParseState.FinishFailure("Action not provided", resolution = "Please specify an action to run.", positional = context.positional, actions = target.actionInfo)
        }
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