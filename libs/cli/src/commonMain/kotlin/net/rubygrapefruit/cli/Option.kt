package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * An option of type <T>.
 */
sealed interface Option<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

/**
 * A string option that is not required.
 */
interface NullableStringOption : Option<String?> {
    /**
     * Returns the value to use with this option is missing.
     */
    fun default(value: String): Option<String>

    fun int(): Option<Int?>
}