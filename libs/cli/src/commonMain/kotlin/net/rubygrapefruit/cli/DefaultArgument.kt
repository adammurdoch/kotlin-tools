package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultArgument(private val name: String, default: String?) : PositionalArgument(), Argument<String> {
    private var value: String? = default

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value ?: throw IllegalStateException()
    }

    override fun accept(arg: String) {
        value = arg
    }

    override fun missing() {
        if (value == null) {
            throw ArgParseException("Argument '$name' not provided")
        }
    }
}