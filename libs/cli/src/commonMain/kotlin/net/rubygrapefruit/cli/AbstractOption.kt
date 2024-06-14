package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal abstract class AbstractOption<T>(protected val name: String, host: Host) : NonPositional(), Option<T> {
    protected val flag = host.option(name)
    private var set = false
    private var value: T? = null

    override fun toString(): String {
        return "$name <value>"
    }

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (set) {
            value!!
        } else {
            convert(null)
        }
    }

    override fun accept(args: List<String>): ParseResult {
        val arg = args.first()
        if (arg != flag) {
            return ParseResult.Nothing
        }
        if (args.size == 1) {
            return ParseResult(1, ArgParseException("Value missing for option $flag"))
        }
        if (value != null) {
            return ParseResult(2, ArgParseException("Option $flag already provided"))
        }
        value = convert(args[1])
        set = true

        return ParseResult(2, null)
    }

    protected abstract fun convert(arg: String?): T
}