package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultNullableOption<T : Any>(
    matcher: OptionMatcher<T>,
    help: String?,
    private val owner: Action,
) : AbstractOption<T>(matcher, help), NullableOption<T> {

    override fun whenAbsent(default: T): Option<T> {
        return owner.replace(this, DefaultOption(matcher, help, default))
    }

    override fun required(): Option<T> {
        return owner.replace(this, RequiredOption(matcher, help))
    }

    override fun repeated(): ListOption<T> {
        return owner.replace(this, DefaultListOption(matcher))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }
}