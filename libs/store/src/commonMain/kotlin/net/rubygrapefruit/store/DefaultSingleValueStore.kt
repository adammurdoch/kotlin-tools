package net.rubygrapefruit.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleDecoder
import net.rubygrapefruit.io.codec.SimpleEncoder

private const val version: UShort = 1u

internal class DefaultSingleValueStore<T>(
    private val name: String,
    private val index: RegularFile,
    private val data: RegularFile,
    private val serializer: KSerializer<T>
) : SingleValueStore<T> {
    override fun get(): T? {
        val address = readEntries { it[name] }
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
        updateIndex {
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
        updateIndex {
            it[name] = address
        }
    }

    private fun <T> readEntries(query: (Map<String, UInt>) -> T): T {
        val entries = readEntries()
        return query(entries)
    }

    private fun updateIndex(update: (MutableMap<String, UInt>) -> Unit) {
        val entries = readEntries()
        update(entries)
        index.withContent { content ->
            val encoder = SimpleEncoder(content.writeStream)
            encoder.ushort(version)
            encoder.string(Json.encodeToString(serializer(), entries))
        }
    }

    private fun readEntries(): MutableMap<String, UInt> {
        return index.withContent { content ->
            if (content.length() == 0u) {
                mutableMapOf()
            } else {
                val decoder = SimpleDecoder(content.readStream)
                require(decoder.ushort() == version)
                Json.decodeFromString<MutableMap<String, UInt>>(decoder.string())
            }
        }.get()
    }
}
