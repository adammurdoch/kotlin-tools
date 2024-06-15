package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A parameter of type <T>.
 */
sealed interface Parameter<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}