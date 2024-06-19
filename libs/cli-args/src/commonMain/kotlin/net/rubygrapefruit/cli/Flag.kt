package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A boolean flag.
 */
sealed interface Flag {
    /**
     * The string to use on the command-line to enable this flag.
     */
    val enableUsage: String

    /**
     * Returns a flag that uses the given default value when this flag is not present in the input.
     */
    fun whenAbsent(default: Boolean): Flag

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean
}