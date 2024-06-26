package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class NullableActionParameter<T : Action>(
    options: Map<String, ChoiceDetails<T>>,
    parameters: Map<String, ChoiceDetails<T>>,
    host: Host
) : AbstractActionParameter<T>(options, parameters, host), Parameter<T?> {

    override fun missing(): ArgParseException? {
        return null
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return action
    }
}
