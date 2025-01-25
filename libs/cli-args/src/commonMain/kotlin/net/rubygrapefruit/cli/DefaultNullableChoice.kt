package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultNullableChoice<T : Any>(
    choices: ChoiceFlagMatcher<T>,
    private val owner: Action
) : AbstractChoice<T>(choices), NullableOption<T> {

    override fun whenAbsent(default: T): Option<T> {
        return owner.replace(this, DefaultChoice(matcher, default))
    }

    override fun required(): Option<T> {
        return owner.replace(this, RequiredChoice(matcher))
    }

    override fun repeated(): ListOption<T> {
        return owner.replace(this, DefaultListOption(matcher))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }
}
