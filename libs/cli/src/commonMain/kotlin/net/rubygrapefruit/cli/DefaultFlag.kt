package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultFlag(name: String, host: Host, default: Boolean) : NonPositional(), Flag {
    private val enableFlag = host.option(name)
    private val disableFlag = host.option("no-$name")
    private var value: Boolean = default

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return value
    }

    override fun accept(args: List<String>): Int {
        val arg = args.first()
        return if (arg == enableFlag) {
            value = true
            1
        } else if (arg == disableFlag) {
            value = false
            1
        } else {
            0
        }
    }
}