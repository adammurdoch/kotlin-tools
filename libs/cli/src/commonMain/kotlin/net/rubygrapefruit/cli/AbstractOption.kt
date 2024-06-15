package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal abstract class AbstractOption<T>(protected val names: List<String>, protected val help: String?, host: Host) : NonPositional(), Option<T> {
    private val flags = names.map { host.option(it) }
    private var set = false
    private var value: T? = null

    override fun toString(): String {
        return "${flags.first()} <value>"
    }

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (set) {
            value!!
        } else {
            convert(flags.first(), null)
        }
    }

    override fun usage(): List<OptionUsage> {
        return listOf(OptionUsage(flags.joinToString(", ") { "$it <value>" }, help))
    }

    override fun accept(args: List<String>): ParseResult {
        val arg = args.first()
        if (!flags.contains(arg)) {
            return ParseResult.Nothing
        }
        if (args.size == 1) {
            return ParseResult(1, ArgParseException("Value missing for option $arg"))
        }
        if (value != null) {
            return ParseResult(2, ArgParseException("Value for option $arg already provided"))
        }
        value = convert(arg, args[1])
        set = true

        return ParseResult.Two
    }

    protected abstract fun convert(flag: String, arg: String?): T
}