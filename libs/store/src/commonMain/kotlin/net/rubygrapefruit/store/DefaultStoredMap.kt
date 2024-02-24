package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class DefaultStoredMap<K, V>(
    name: String,
    index: Index,
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : StoredMap<K, V> {
    private val index = index.map(name)

    override fun get(key: K): V? {
        val encodedKey = Json.encodeToString(keySerializer, key)
        val block = index.get().get(encodedKey)
        return if (block == null) {
            null
        } else {
            index.data.read(block, valueSerializer)
        }
    }

    override fun set(key: K, value: V) {
        val block = index.data.append(value, valueSerializer)
        val encodedKey = Json.encodeToString(keySerializer, key)
        index.set(encodedKey, block)
    }

    override fun remove(key: K) {
        val encodedKey = Json.encodeToString(keySerializer, key)
        index.remove(encodedKey)
    }

    override fun discard() {
        index.discard()
    }

    override fun entries(): List<Pair<K, V>> {
        val entries = index.get()
        val result = ArrayList<Pair<K, V>>(entries.size)
        for (entry in entries) {
            val key = Json.decodeFromString(keySerializer, entry.key)
            result.add(Pair(key, index.data.read(entry.value, valueSerializer)))
        }
        return result
    }
}
