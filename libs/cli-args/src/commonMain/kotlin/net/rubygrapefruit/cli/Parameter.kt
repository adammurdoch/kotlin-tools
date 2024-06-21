package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A parameter of type [T].
 */
sealed interface Parameter<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

/**
 * A parameter of type [T] that must be present in the input.
 */
interface RequiredParameter<T : Any> : Parameter<T> {
    /**
     * Returns a parameter that uses the given default value when this parameter is not present in the input.
     */
    fun whenAbsent(default: T): Parameter<T>

    /**
     * Returns a parameter that has `null` value when this parameter is not present in the inoput.
     */
    fun optional(): Parameter<T?>
}

/**
 * A parameter of type List<T>.
 */
interface ListParameter<T : Any> : Parameter<List<T>> {
    /**
     * Returns a parameter that uses the given default value when this parameter is not present in the input.
     */
    fun whenAbsent(default: List<T>): Parameter<List<T>>

    /**
     * Returns a parameter that requires at least one argument.
     */
    fun required(): Parameter<List<T>>
}
