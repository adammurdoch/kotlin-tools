package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal abstract class AbstractOption<T>(protected val name: String) : NonPositional(), Option<T> {
    protected val flag = "--$name"
    private var set = false
    private var value: T? = null

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (set) {
            value!!
        } else {
            convert(null)
        }
    }

    override fun accept(args: List<String>): Int {
        val arg = args.first()
        if (arg != flag) {
            return 0
        }
        if (value != null) {
            throw ArgParseException("Option $flag already provided")
        }
        if (args.size == 1) {
            throw ArgParseException("Argument missing for option $flag")
        }
        value = convert(args[1])
        set = true

        return 2
    }

    protected abstract fun convert(arg: String?): T
}