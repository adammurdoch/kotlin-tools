@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.ElementType
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.file.regularFile
import net.rubygrapefruit.io.codec.SimpleCodec

/**
 * A [Store] is a persistent container of data stored in a single directory.
 *
 * A store can contain one or more of the following entries:
 *
 * - [StoredValue]: A mutable value of type T.
 * - [StoredMap]: A mutable map with keys of type K and values of type T.
 */
class Store private constructor(
    directory: Directory,
    metadataFile: RegularFile,
) : AutoCloseable {
    private val codec = SimpleCodec()
    private val metadata = MetadataFile(metadataFile, codec)
    private val index = Index(directory.file("log_${metadata.currentGeneration}.bin"), codec)
    private val data = DataFile(directory.file("data_${metadata.currentGeneration}.bin"), codec)

    companion object {
        /**
         * Opens the store in the given directory.
         */
        fun open(directory: Directory, discard: Boolean = false): Store {
            directory.createDirectories()
            val metadata = directory.file("store.bin")
            if (discard) {
                metadata.delete()
                for (entry in directory.listEntries().get()) {
                    if (entry.type == ElementType.RegularFile) {
                        entry.toFile().delete()
                    }
                }
            } else if (directory.listEntries().get().isNotEmpty() && !metadata.metadata().regularFile) {
                unrecognizedFormat(directory)
            }
            return Store(directory, metadata)
        }
    }

    /**
     * Opens the [StoredValue] with the given name and type, creating it if it does not exist.
     */
    fun <T : Any> value(name: String, serializer: KSerializer<T>): StoredValue<T> {
        return DefaultStoredValue(name, index, data, serializer)
    }

    /**
     * Opens the [StoredValue] with the given name and type, creating it if it does not exist.
     */
    inline fun <reified T : Any> value(name: String): StoredValue<T> {
        return value(name, serializer())
    }

    /**
     * Opens the [StoredMap] with the given name and types, creating it if it does not exist.
     */
    fun <K : Any, V : Any> map(name: String, keySerializer: KSerializer<K>, valueSerializer: KSerializer<V>): StoredMap<K, V> {
        return DefaultStoredMap(name, index, data, keySerializer, valueSerializer)
    }

    /**
     * Opens the [StoredMap] with the given name and types, creating it if it does not exist.
     */
    inline fun <reified K : Any, reified V : Any> map(name: String): StoredMap<K, V> {
        return map(name, serializer(), serializer())
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