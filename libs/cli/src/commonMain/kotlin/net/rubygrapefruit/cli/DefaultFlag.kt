package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultFlag(name: String, private val help: String?, host: Host, default: Boolean) : NonPositional(), Flag {
    private val enableFlag = host.option(name)
    private val disableFlag = host.option("no-$name")
    private var value: Boolean = default

    override val enableUsage: String
        get() = enableFlag

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return value
    }

    override fun usage(): List<OptionUsage> {
        return listOf(OptionUsage("$enableFlag/$disableFlag", help))
    }

    override fun accept(args: List<String>): ParseResult {
        val arg = args.first()
        return if (arg == enableFlag) {
            value = true
            ParseResult.One
        } else if (arg == disableFlag) {
            value = false
            ParseResult.One
        } else {
            ParseResult.Nothing
        }
    }
}