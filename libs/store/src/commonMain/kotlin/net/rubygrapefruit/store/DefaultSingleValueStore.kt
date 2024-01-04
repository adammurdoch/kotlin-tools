package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.rubygrapefruit.file.MissingEntry
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.file.Success
import net.rubygrapefruit.io.codec.SimpleDecoder
import net.rubygrapefruit.io.codec.SimpleEncoder

private const val version: UShort = 1u

internal class DefaultSingleValueStore<T>(
    private val file: RegularFile,
    private val serializer: KSerializer<T>
) : SingleValueStore<T> {
    override fun get(): T? {
        val result = file.readBytes { stream ->
            val decoder = SimpleDecoder(stream)
            require(decoder.ushort() == version)
            Success(Json.decodeFromString(serializer, decoder.string()))
        }
        return if (result is MissingEntry) {
            null
        } else {
            result.get()
        }
    }

    override fun discard() {
        file.delete()
    }

    override fun set(value: T) {
        file.writeBytes { stream ->
            val encoder = SimpleEncoder(stream)
            encoder.ushort(version)
            encoder.string(Json.encodeToString(serializer, value))
        }
    }
}
