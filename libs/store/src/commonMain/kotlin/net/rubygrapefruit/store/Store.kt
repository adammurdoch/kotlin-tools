@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.MissingEntry

class Store private constructor(private val directory: Directory) : AutoCloseable {
    companion object {
        fun open(directory: Directory): Store {
            directory.createDirectories()
            return Store(directory)
        }
    }

    fun <T : Any> value(name: String, serializer: KSerializer<T>): SingleValueStore<T> {
        val file = directory.file(name)

        return object : SingleValueStore<T> {
            override fun get(): T? {
                val result = file.readText()
                return if (result is MissingEntry) {
                    null
                } else {
                    Json.decodeFromString(serializer, result.get())
                }
            }

            override fun discard() {
                file.delete()
            }

            override fun set(value: T) {
                file.writeText(Json.encodeToString(serializer, value))
            }
        }
    }

    override fun close() {
    }
}