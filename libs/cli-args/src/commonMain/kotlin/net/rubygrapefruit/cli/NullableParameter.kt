package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal open class NullableParameter<T : Any>(
    name: String,
    help: String?,
    host: Host,
    converter: StringConverter<T>
) : AbstractParameter<T>(name, help, true, host, converter), Parameter<T?> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return value
    }

    override fun missing(): ArgParseException? {
        return null
    }
}