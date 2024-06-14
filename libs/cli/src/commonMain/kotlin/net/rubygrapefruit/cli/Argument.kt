package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * An argument of type <T>.
 */
sealed interface Argument<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}