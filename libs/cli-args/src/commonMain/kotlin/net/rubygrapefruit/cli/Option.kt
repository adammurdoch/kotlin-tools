package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A named parameter of type <T>.
 */
sealed interface Option<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

/**
 * A named parameter of type <T> that is not required.
 */
interface NullableOption<T : Any> : Option<T?> {
    /**
     * Returns an option that uses the given default value when this option is not present in the input.
     */
    fun whenAbsent(default: T): Option<T>

    /**
     * Returns an option that must be present in the input.
     */
    fun required(): Option<T>
}
