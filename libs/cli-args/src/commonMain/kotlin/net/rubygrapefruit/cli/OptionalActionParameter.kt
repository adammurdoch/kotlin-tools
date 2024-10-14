package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class OptionalActionParameter<T : Action>(
    actions: ActionSet<T>,
    host: Host,
    private val default: T
) : AbstractActionParameter<T>(actions, host), Parameter<T> {

    override fun whenMissing(context: ParseContext): FinishResult {
        return FinishResult.Success
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return selected ?: default
    }
}
