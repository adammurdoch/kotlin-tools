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
            return ParseState.Continue(1, ActionParseState(nestedContext, action.value))
        }

        if (host.isOption(name)) {
            return ParseState.Nothing
        }

        if (actions.default != null) {
            val nestedContext = context.nested(target, actions.default.value.positional())
            return ParseState.Continue(0, ActionParseState(nestedContext, actions.default.value))
        }

        return ParseState.Failure(1, "Unknown action: $name", positional = context.positional, actions = target.actionInfo)
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (actions.default != null) {
            val nestedContext = context.nested(target, actions.default.value.positional())
            ActionParseState(nestedContext, actions.default.value).endOfInput()
        } else {
            ParseState.FinishFailure("Action not provided", resolution = "Please specify an action to run.", positional = context.positional, actions = target.actionInfo)
        }
    }

    private class ActionParseState(private val context: ParseContext, private val action: Action) : ParseState {
        override fun parseNextValue(args: List<String>): ParseState.Result {
            TODO("Not yet implemented")
        }

        override fun endOfInput(): ParseState.FinishResult {
            TODO("Not yet implemented")
        }
    }
}