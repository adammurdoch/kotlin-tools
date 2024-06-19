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
     * Returns an option that uses the given default value when this option is not present in the input.
     */
    fun whenAbsent(default: T): Option<T>
}

/**
 * A string option that is not required.
 */
interface NullableStringOption : NullableOption<String> {

}