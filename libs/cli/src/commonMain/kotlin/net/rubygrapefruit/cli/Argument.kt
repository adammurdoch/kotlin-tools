package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * An argument.
 */
sealed interface Argument {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String
}