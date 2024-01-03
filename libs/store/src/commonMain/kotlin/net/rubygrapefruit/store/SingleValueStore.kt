package net.rubygrapefruit.store

interface SingleValueStore<T> {
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