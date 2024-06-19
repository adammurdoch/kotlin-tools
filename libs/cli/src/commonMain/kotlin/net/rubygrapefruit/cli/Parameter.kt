package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

/**
 * A parameter of type <T>.
 */
sealed interface Parameter<T : Any> {
    /**
     * Returns a parameter that uses the given default value when this parameter is not present in the input.
     */
    fun whenAbsent(default: T): Parameter<T>

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

/**
 * A string parameter.
 */
interface StringParameter<T : Any> : Parameter<T> {
    fun int(): Parameter<Int>
    fun path(): Parameter<FilePath>
}

/**
 * A parameter of type List<T>.
 */
interface ListParameter<T : Any> : Parameter<List<T>> {
    /**
     * Returns a parameter that requires at least one argument.
     */
    fun required(): Parameter<List<T>>
}

interface StringListParameter : ListParameter<String> {
    fun path(): ListParameter<FilePath>
}