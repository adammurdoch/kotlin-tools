package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A boolean flag.
 */
sealed interface Flag {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean
}