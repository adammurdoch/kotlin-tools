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
        val action = actions.named[name]
        if (action != null) {
            val nestedContext = context.nested(target, listOf(NameUsage(name)) + action.value.positional())
            return ParseState.Continue(1, stateFor(action.value, nestedContext))
        }

        if (host.isOption(name)) {
            return ParseState.Nothing
        }

        if (actions.default != null) {
            val nestedContext = context.nested(target, actions.default.value.positional())
            return ParseState.Continue(0, stateFor(actions.default.value, nestedContext))
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
        return action.state(nestedContext) {
            target.value(action)
        }
    }

}