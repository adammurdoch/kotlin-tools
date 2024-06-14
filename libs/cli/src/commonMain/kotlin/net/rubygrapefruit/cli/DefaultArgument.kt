package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultArgument(private val name: String, default: String?) : Positional(), Argument<String> {
    private var value: String? = default

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value ?: throw IllegalStateException()
    }

    override fun accept(args: List<String>): Int {
        value = args.first()
        return 1
    }

    override fun missing() {
        if (value == null) {
            throw ArgParseException("Argument '$name' not provided")
        }
    }
}