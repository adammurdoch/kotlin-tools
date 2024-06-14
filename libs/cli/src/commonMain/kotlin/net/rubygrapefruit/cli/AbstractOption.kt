package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal abstract class AbstractOption<T>(protected val name: String) : NonPositional(), Option<T> {
    private val flag = "--$name"
    private var value: String? = null

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return convert(value)
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

    protected abstract fun convert(arg: String?): T
}