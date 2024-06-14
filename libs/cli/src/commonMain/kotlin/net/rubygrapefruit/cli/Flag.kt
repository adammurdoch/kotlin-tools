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

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean
}