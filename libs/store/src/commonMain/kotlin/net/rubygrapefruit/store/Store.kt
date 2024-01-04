@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import net.rubygrapefruit.file.Directory

class Store private constructor(private val directory: Directory) : AutoCloseable {
    companion object {
        fun open(directory: Directory): Store {
            directory.createDirectories()
            return Store(directory)
        }
    }

    fun <T : Any> value(name: String, serializer: KSerializer<T>): SingleValueStore<T> {
        val file = directory.file(name)
        return DefaultSingleValueStore(file, serializer)
    }

    override fun close() {
    }
}