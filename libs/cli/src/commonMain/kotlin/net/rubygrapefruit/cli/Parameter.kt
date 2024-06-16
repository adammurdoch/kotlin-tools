package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A parameter of type <T>.
 */
sealed interface Parameter<T> {
    /**
     * Returns a parameter that uses the given default value when this parameter is not present in the input.
     */
    fun whenAbsent(default: T): Parameter<T>

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}