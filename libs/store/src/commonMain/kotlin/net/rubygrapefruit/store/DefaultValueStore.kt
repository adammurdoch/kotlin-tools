package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer

internal class DefaultValueStore<T>(
    name: String,
    index: Index,
    private val data: DataFile,
    private val serializer: KSerializer<T>
) : ValueStore<T> {
    private val index = index.value(name)

    override fun get(): T? {
        val address = index.get()
        return if (address == null) {
            null
        } else {
            data.read(address, serializer)
        }
    }

    override fun discard() {
        index.discard()
    }

    override fun set(value: T) {
        val address = data.write(value, serializer)
        index.set(address)
    }
}
