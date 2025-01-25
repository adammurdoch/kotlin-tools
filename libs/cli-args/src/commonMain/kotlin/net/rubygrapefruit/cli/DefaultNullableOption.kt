package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultNullableOption<T : Any>(
    matcher: OptionMatcher<T>,
    private val owner: Action,
) : AbstractOption<T>(matcher), NullableOption<T> {

    override fun whenAbsent(default: T): Option<T> {
        return owner.replace(this, DefaultOption(matcher, default))
    }

    override fun required(): Option<T> {
        return owner.replace(this, RequiredOption(matcher))
    }

    override fun repeated(): ListOption<T> {
        return owner.replace(this, DefaultListOption(matcher))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }
}