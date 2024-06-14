package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * An option of type <T>.
 */
sealed interface Option<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

interface NullableStringOption : Option<String?> {
    fun default(value: String): Option<String>
}