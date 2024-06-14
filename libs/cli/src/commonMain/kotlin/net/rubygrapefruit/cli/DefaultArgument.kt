package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultArgument(private val name: String, private val host: Host, default: String?) : Positional(), Argument<String> {
    private var value: String? = default

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value ?: throw IllegalStateException()
    }

    override fun accept(args: List<String>): Int {
        val candidate = args.first()
        return if (host.isOption(candidate)) {
            0
        } else {
            value = candidate
            1
        }
    }

    override fun missing() {
        if (value == null) {
            throw ArgParseException("Argument '$name' not provided")
        }
    }
}