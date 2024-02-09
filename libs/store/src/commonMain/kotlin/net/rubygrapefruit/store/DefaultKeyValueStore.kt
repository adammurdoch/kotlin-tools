package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer

internal class DefaultKeyValueStore<K, V>(
    name: String,
    index: Index,
    data: DataFile,
    keySerializer: KSerializer<K>,
    valueSerializer: KSerializer<V>
) : KeyValueStore<K, V> {
    override fun get(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun set(key: K, value: V) {
        TODO("Not yet implemented")
    }

    override fun remove(key: K) {
        TODO("Not yet implemented")
    }

    override fun discard() {
        TODO("Not yet implemented")
    }

    override fun entries(): List<Pair<K, V>> {
        TODO("Not yet implemented")
    }
}
