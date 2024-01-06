@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.RegularFile

class Store private constructor(
    indexFile: RegularFile,
    private val data: RegularFile
) : AutoCloseable {
    private val index = Index(indexFile)

    companion object {
        fun open(directory: Directory): Store {
            directory.createDirectories()
            val index = directory.file("index.bin")
            val data = directory.file("data.bin")
            return Store(index, data)
        }
    }

    fun <T : Any> value(name: String, serializer: KSerializer<T>): SingleValueStore<T> {
        return DefaultSingleValueStore(name, index, data, serializer)
    }

    inline fun <reified T : Any> value(name: String): SingleValueStore<T> {
        return value(name, serializer())
    }

    fun accept(visitor: ContentVisitor) {
        index.accept(visitor)
    }

    override fun close() {
    }
}