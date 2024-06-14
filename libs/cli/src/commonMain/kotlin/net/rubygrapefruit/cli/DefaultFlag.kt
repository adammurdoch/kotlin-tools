package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultFlag(name: String, default: Boolean) : Flag {
    private val enableFlag = "--$name"
    private val disableFlag = "--no-$name"
    private var value: Boolean = default

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return value
    }

    fun accept(arg: String): Boolean {
        return if (arg == enableFlag) {
            value = true
            true
        } else if (arg == disableFlag) {
            value = false
            true
        } else {
            false
        }
    }
}