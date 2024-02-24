package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer

internal class DefaultStoredValue<T>(
    name: String,
    index: Index,
    private val serializer: KSerializer<T>
) : StoredValue<T> {
    private val index = index.value(name)

    override fun get(): T? {
        val block = index.get()
        return if (block == null) {
            null
        } else {
            index.data.read(block, serializer)
        }
    }

    override fun discard() {
        index.discard()
    }

    override fun set(value: T) {
        val block = index.data.append(value, serializer)
        index.set(block)
    }
}
