package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.rubygrapefruit.file.MissingEntry
import net.rubygrapefruit.file.RegularFile

internal class DefaultSingleValueStore<T>(
    private val file: RegularFile,
    private val serializer: KSerializer<T>
) : SingleValueStore<T> {
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
