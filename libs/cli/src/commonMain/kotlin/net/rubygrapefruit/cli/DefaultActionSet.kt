package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionSet(private val host: Host) : Positional(), Argument<Action>, Action.Actions {
    private val actions = mutableMapOf<String, ActionDetails>()
    private var action: Action? = null

    override fun action(name: String, action: Action, help: String?) {
        actions[name] = ActionDetails(action, help)
    }

    override fun accept(args: List<String>): Int {
        val name = args.first()
        if (host.isOption(name)) {
            return 0
        }
        if (!actions.containsKey(name)) {
            throw ArgParseException("Unknown action: $name", actions = actionInfo)
        }
        action = actions[name]!!.action
        return 1 + action!!.maybeParse(args.drop(1))
    }

    private val actionInfo
        get() = actions.map { SubActionInfo(it.key, it.value.help) }

    override fun missing() {
        throw ArgParseException("Action not provided", resolution = "Please specify an action to run.", actions = actionInfo)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Action {
        return action ?: throw IllegalStateException()
    }

    private class ActionDetails(val action: Action, val help: String?)
}