package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer

internal class DefaultSingleValueStore<T>(
    private val name: String,
    private val index: Index,
    private val data: DataFile,
    private val serializer: KSerializer<T>
) : SingleValueStore<T> {
    override fun get(): T? {
        val address = index.query { it[name] }
        return if (address == null) {
            null
        } else {
            data.read(address, serializer)
        }
    }

    override fun discard() {
        index.update {
            it.remove(name)
        }
    }

    override fun set(value: T) {
        val address = data.write(value, serializer)
        index.update {
            it[name] = address
        }
    }
}
