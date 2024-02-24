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

    // The map of key (as type K, rather than String) to address
    private val entries = readEntries()

    override fun get(key: K): V? {
        val block = entries[key]
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
        entries[key] = block
    }

    override fun remove(key: K) {
        val encodedKey = Json.encodeToString(keySerializer, key)
        index.remove(encodedKey)
        entries.remove(key)
    }

    override fun discard() {
        index.discard()
        entries.clear()
    }

    override fun entries(): List<Pair<K, V>> {
        val result = ArrayList<Pair<K, V>>(entries.size)
        for (entry in entries) {
            result.add(Pair(entry.key, index.data.read(entry.value, valueSerializer)))
        }
        return result
    }

    private fun readEntries(): MutableMap<K, Block> {
        val entries = index.get()
        val result = LinkedHashMap<K, Block>(entries.size)
        for (item in entries.entries) {
            val key = Json.decodeFromString(keySerializer, item.key)
            result[key] = item.value
        }
        return result
    }
}
