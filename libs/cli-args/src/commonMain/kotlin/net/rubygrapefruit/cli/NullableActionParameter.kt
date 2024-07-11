package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class NullableActionParameter<T : Action>(
    actions: ActionSet<T>,
    host: Host
) : AbstractActionParameter<T>(actions, host), Parameter<T?> {

    override fun whenMissing(context: ParseContext): ArgParseException? {
        return null
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return action
    }
}
