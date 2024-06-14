package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionSet(private val host: Host) : Positional(), Argument<Action>, Action.Actions {
    private val actions = mutableMapOf<String, ActionDetails>()
    private var action: Action? = null

    override fun action(name: String, action: Action, help: String?) {
        host.validate(name, "an action name")
        actions[name] = ActionDetails(action, help)
    }

    override fun accept(args: List<String>): ParseResult {
        val name = args.first()
        if (host.isOption(name)) {
            return ParseResult.Nothing
        }
        if (!actions.containsKey(name)) {
            return ParseResult(1, ArgParseException("Unknown action: $name", actions = actionInfo))
        }
        action = actions[name]!!.action
        val result = action!!.maybeParse(args.drop(1))
        return ParseResult(1 + result.count, result.failure)
    }

    private val actionInfo
        get() = actions.map { SubActionUsage(it.key, it.value.help) }

    override fun missing(): ArgParseException {
        return ArgParseException("Action not provided", resolution = "Please specify an action to run.", actions = actionInfo)
    }

    override fun usage(): PositionalUsage {
        return PositionalUsage("<action>", "action", null)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Action {
        return action ?: throw IllegalStateException()
    }

    private class ActionDetails(val action: Action, val help: String?)
}