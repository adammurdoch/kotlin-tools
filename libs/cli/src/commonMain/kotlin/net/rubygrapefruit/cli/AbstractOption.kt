package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal abstract class AbstractOption<T>(protected val names: List<String>, protected val help: String?, private val host: Host) : NonPositional(), Option<T> {
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
            convert(flags.first(), null).getOrThrow()
        }
    }

    override fun usage(): List<OptionUsage> {
        val usage = SingleOptionUsage(flags.joinToString(", ") { "$it <value>" }, help, flags)
        return listOf(OptionUsage(usage.usage, help, listOf(usage)))
    }

    override fun accept(args: List<String>): ParseResult {
        val arg = args.first()
        if (!flags.contains(arg)) {
            return ParseResult.Nothing
        }
        if (args.size == 1 || host.isOption(args[1])) {
            return ParseResult(1, ArgParseException("Value missing for option $arg"), true)
        }
        if (value != null) {
            return ParseResult(2, ArgParseException("Value for option $arg already provided"), true)
        }
        val result = convert(arg, args[1])
        if (result.isFailure) {
            return ParseResult(2, result.exceptionOrNull() as ArgParseException, true)
        }
        value = result.getOrThrow()
        set = true
        return ParseResult.Two
    }

    protected abstract fun convert(flag: String, arg: String?): Result<T>
}