package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionSet : Positional(), Argument<Action>, Action.Actions {
    private val actions = mutableMapOf<String, ActionDetails>()
    private var action: Action? = null

    override fun action(name: String, action: Action, help: String?) {
        actions[name] = ActionDetails(action, help)
    }

    override fun accept(args: List<String>): Int {
        val name = args.first()
        if (!actions.containsKey(name)) {
            throw ArgParseException("Unknown action '$name'")
        }
        action = actions[name]!!.action
        return 1 + action!!.maybeParse(args.drop(1))
    }

    override fun missing() {
        throw ArgParseException("Action not provided", "Please specify an action to run.", actions.map { SubActionInfo(it.key, it.value.help) })
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Action {
        return action ?: throw IllegalStateException()
    }

    private class ActionDetails(val action: Action, val help: String?)
}