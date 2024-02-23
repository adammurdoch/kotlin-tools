package net.rubygrapefruit.store

/**
 * Represents a single mutable value of type [T].
 */
interface StoredValue<T> {
    /**
     * Returns the current value, if any.
     */
    fun get(): T?

    /**
     * Replaces any current value.
     */
    fun set(value: T)

    /**
     * Discards the current value.
     */
    fun discard()
}