package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A positional parameter of type [T].
 */
sealed interface Parameter<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

/**
 * A positional parameter of type [T] that must be present in the input.
 */
interface RequiredParameter<T : Any> : Parameter<T> {
    /**
     * Returns a parameter that uses the given default value when the parameter is not present in the input.
     */
    fun whenAbsent(default: T): Parameter<T>

    /**
     * Returns a parameter that has `null` value when the parameter is not present in the input.
     */
    fun optional(): Parameter<T?>

    /**
     * Returns a parameter that can be repeated one or more times.
     */
    fun repeated(): ListParameter<T>
}

/**
 * A positional parameter of type List<T> that requires one or more values.
 */
interface ListParameter<T : Any> : Parameter<List<T>> {
    /**
     * Returns a parameter that uses the given default value when the parameter is not present in the input.
     */
    fun whenAbsent(default: List<T>): Parameter<List<T>>

    /**
     * Returns a parameter that uses an empty list when the parameter is not present in the input.
     */
    fun optional(): Parameter<List<T>>
}

/**
 * A positional parameter of type List<T> that allows zeor or more values.
 */
interface OptionalListParameter<T : Any> : Parameter<List<T>> {
    /**
     * Returns a parameter that uses an empty list when the parameter is not present in the input.
     */
    fun required(): Parameter<List<T>>
}
