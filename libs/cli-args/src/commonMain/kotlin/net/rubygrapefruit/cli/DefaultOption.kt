package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultOption<T : Any>(
    names: List<String>,
    help: String?,
    val default: T,
    host: Host,
    converter: StringConverter<T>
) : AbstractOption<T>(names, help, host, converter), Option<T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: default
    }
}