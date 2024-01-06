@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.RegularFile

class Store private constructor(
    private val index: RegularFile,
    private val data: RegularFile
) : AutoCloseable {
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

    override fun close() {
    }
}