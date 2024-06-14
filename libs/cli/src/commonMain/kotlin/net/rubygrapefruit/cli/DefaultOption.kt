package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultOption(name: String) : NonPositional(), Option<String?> {
    private val flag = "--$name"
    private var value: String? = null

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return value
    }

    override fun accept(args: List<String>): Int {
        val arg = args.first()
        if (arg != flag) {
            return 0
        }
        if (args.size == 1) {
            throw ArgParseException("Argument missing for option $flag")
        }
        value = args[1]
        return 2
    }
}