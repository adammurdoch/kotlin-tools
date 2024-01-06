package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleDecoder
import net.rubygrapefruit.io.codec.SimpleEncoder

internal class DefaultSingleValueStore<T>(
    private val name: String,
    private val index: Index,
    private val data: RegularFile,
    private val serializer: KSerializer<T>
) : SingleValueStore<T> {
    override fun get(): T? {
        val address = index.query { it[name] }
        return if (address == null) {
            null
        } else {
            data.withContent { content ->
                content.seek(address)
                val decoder = SimpleDecoder(content.readStream)
                Json.decodeFromString(serializer, decoder.string())
            }.get()
        }
    }

    override fun discard() {
        index.update {
            it.remove(name)
        }
    }

    override fun set(value: T) {
        val address = data.withContent { content ->
            content.seekToEnd()
            val pos = content.currentPosition
            val encoder = SimpleEncoder(content.writeStream)
            encoder.string(Json.encodeToString(serializer, value))
            pos
        }.get()
        index.update {
            it[name] = address
        }
    }
}
