package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionSet : PositionalArgument(), Argument<Action>, Action.Actions {
    private val actions = mutableMapOf<String, Action>()
    private var action: Action? = null

    override fun action(name: String, action: Action) {
        actions[name] = action
    }

    override fun accept(arg: String) {
        if (!actions.containsKey(arg)) {
            throw ArgParseException("Unknown command '$arg'")
        }
        action = actions[arg]
    }

    override fun missing() {
        throw ArgParseException("Command not provided")
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Action {
        return action ?: throw IllegalStateException()
    }
}