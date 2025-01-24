package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultNullableOption<T : Any>(
    names: List<String>,
    help: String?,
    host: Host,
    private val owner: Action,
    converter: StringConverter<T>,
) : AbstractOption<T>(names, help, host, converter), NullableOption<T> {

    override fun whenAbsent(default: T): Option<T> {
        return owner.replace(this, DefaultOption(names, help, default, host, converter))
    }

    override fun required(): Option<T> {
        return owner.replace(this, RequiredOption(names, help, host, converter))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }
}