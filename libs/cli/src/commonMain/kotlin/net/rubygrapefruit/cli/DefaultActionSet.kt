package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionSet : Positional(), Argument<Action>, Action.Actions {
    private val actions = mutableMapOf<String, Action>()
    private var action: Action? = null

    override fun action(name: String, action: Action, help: String?) {
        actions[name] = action
    }

    override fun accept(args: List<String>): Int {
        val name = args.first()
        if (!actions.containsKey(name)) {
            throw ArgParseException("Unknown action '$name'")
        }
        action = actions[name]
        return 1 + action!!.maybeParse(args.drop(1))
    }

    override fun missing() {
        throw ArgParseException("Action not provided")
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Action {
        return action ?: throw IllegalStateException()
    }
}