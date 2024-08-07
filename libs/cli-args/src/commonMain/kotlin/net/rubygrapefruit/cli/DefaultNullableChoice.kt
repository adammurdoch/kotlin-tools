package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultNullableChoice<T : Any>(
    private val choices: List<ChoiceDetails<T>>,
    private val owner: Action
) : AbstractChoice<T>(choices), NullableOption<T> {

    override fun whenAbsent(default: T): Option<T> {
        return owner.replace(this, DefaultChoice(choices, default))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }
}
