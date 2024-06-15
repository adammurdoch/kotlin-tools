package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultFlag(names: List<String>, private val help: String?, host: Host, default: Boolean) : NonPositional(), Flag {
    private val enableFlags = names.map { host.option(it) }
    private val disableFlags = names.mapNotNull { if (it.length == 1) null else host.option("no-$it") }
    private var value: Boolean = default

    override val enableUsage: String
        get() = enableFlags.first()

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return value
    }

    override fun usage(): List<OptionUsage> {
        return listOf(OptionUsage((enableFlags + disableFlags).joinToString(", "), help))
    }

    override fun accept(args: List<String>): ParseResult {
        val arg = args.first()
        return if (enableFlags.contains(arg)) {
            value = true
            ParseResult.One
        } else if (disableFlags.contains(arg)) {
            value = false
            ParseResult.One
        } else {
            ParseResult.Nothing
        }
    }
}