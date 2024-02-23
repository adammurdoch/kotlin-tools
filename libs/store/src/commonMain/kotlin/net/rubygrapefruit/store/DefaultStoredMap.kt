package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class DefaultStoredMap<K, V>(
    name: String,
    index: Index,
    private val data: DataFile,
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : StoredMap<K, V> {
    private val index = index.keyValue(name)
    // The map of key (as type K, rather than String) to address
    private val entries = readEntries()

    override fun get(key: K): V? {
        val address = entries[key]
        return if (address == null) {
            null
        } else {
            data.read(address, valueSerializer)
        }
    }

    override fun set(key: K, value: V) {
        val address = data.write(value, valueSerializer)
        val encodedKey = Json.encodeToString(keySerializer, key)
        index.set(encodedKey, address)
        entries[key] = address
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
            result.add(Pair(entry.key, data.read(entry.value, valueSerializer)))
        }
        return result
    }

    private fun readEntries(): MutableMap<K, Address> {
        val entries = index.get()
        val result = LinkedHashMap<K, Address>(entries.size)
        for (item in entries.entries) {
            val key = Json.decodeFromString(keySerializer, item.key)
            result[key] = item.value
        }
        return result
    }
}
