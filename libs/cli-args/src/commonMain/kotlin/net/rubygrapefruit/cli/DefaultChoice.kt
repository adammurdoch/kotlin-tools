package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultChoice<T : Any>(
    choices: Map<String, ChoiceDetails<T>>,
    private val default: T
) : AbstractChoice<T>(choices), Option<T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: default
    }
}