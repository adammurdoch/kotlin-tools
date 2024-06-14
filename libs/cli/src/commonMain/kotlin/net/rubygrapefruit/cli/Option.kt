package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * An option of type <T>.
 */
sealed interface Option<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

/**
 * An option of type <T> that is not required.
 */
interface NullableOption<T : Any> : Option<T?> {
    /**
     * Returns the value to use with this option is missing.
     */
    fun default(value: T): Option<T>
}

/**
 * A string option that is not required.
 */
interface NullableStringOption : NullableOption<String> {
    /**
     * Treat this option as an integer option.
     */
    fun int(): NullableOption<Int>
}