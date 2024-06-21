package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class OptionalActionParameter<T : Action>(
    actions: Map<String, ChoiceDetails<T>>,
    host: Host,
    private val default: T
) : AbstractActionParameter<T>(actions, host), Parameter<T> {

    override fun missing(): ArgParseException? {
        return null
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return action ?: default
    }
}