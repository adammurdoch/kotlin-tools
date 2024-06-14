package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultArgument(private val name: String, default: String?) : Argument {
    private var value: String? = default

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value ?: throw IllegalStateException()
    }

    fun accept(arg: String) {
        value = arg
    }

    fun missing() {
        if (value == null) {
            throw ArgParseException("Argument '$name' not provided")
        }
    }
}