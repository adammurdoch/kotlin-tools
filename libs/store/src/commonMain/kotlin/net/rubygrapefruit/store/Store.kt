@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

/**
 * A [Store] is a persistent container of data stored in a single directory.
 *
 * A store can contain one or more of the following entries:
 *
 * - [ValueStore]: A mutable value of type T.
 * - [KeyValueStore]: A mutable map with keys of type K and values of type T.
 */
class Store private constructor(
    indexFile: RegularFile,
    dataFile: RegularFile
) : AutoCloseable {
    private val codec = SimpleCodec()
    private val index = Index(indexFile, codec)
    private val data = DataFile(dataFile, codec)

    companion object {
        /**
         * Opens the store in the given directory.
         */
        fun open(directory: Directory, discard: Boolean = false): Store {
            directory.createDirectories()
            val index = directory.file("index.bin")
            val data = directory.file("data.bin")
            if (discard) {
                index.delete()
                data.delete()
            }
            return Store(index, data)
        }
    }

    /**
     * Opens the [ValueStore] with the given name and type, creating it if it does not exist.
     */
    fun <T : Any> value(name: String, serializer: KSerializer<T>): ValueStore<T> {
        return DefaultValueStore(name, index, data, serializer)
    }

    /**
     * Opens the [ValueStore] with the given name and type, creating it if it does not exist.
     */
    inline fun <reified T : Any> value(name: String): ValueStore<T> {
        return value(name, serializer())
    }

    /**
     * Opens the [KeyValueStore] with the given name and type, creating it if it does not exist.
     */
    fun <K : Any, V : Any> keyValue(name: String, keySerializer: KSerializer<K>, valueSerializer: KSerializer<V>): KeyValueStore<K, V> {
        return DefaultKeyValueStore(name, index, data, keySerializer, valueSerializer)
    }

    /**
     * Opens the [KeyValueStore] with the given name and type, creating it if it does not exist.
     */
    inline fun <reified K : Any, reified V : Any> keyValue(name: String): KeyValueStore<K, V> {
        return keyValue(name, serializer(), serializer())
    }

    /**
     * Visits a summary of the content of this store.
     */
    fun accept(visitor: ContentVisitor) {
        index.accept(visitor)
    }

    override fun close() {
        index.close()
        data.close()
    }
}