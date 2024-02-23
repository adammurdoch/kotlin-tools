package net.rubygrapefruit.store

/**
 * A mutable map with keys of type [K] and values of type [V].
 */
interface StoredMap<K, V> {
    /**
     * Returns the current value of the given key, if any.
     */
    fun get(key: K): V?

    /**
     * Sets the value of the given key.
     */
    fun set(key: K, value: V)

    /**
     * Removes the value for the given key.
     */
    fun remove(key: K)

    /**
     * Discards all values.
     */
    fun discard()

    /**
     * Returns all entries in the store.
     */
    fun entries(): List<Pair<K, V>>
}