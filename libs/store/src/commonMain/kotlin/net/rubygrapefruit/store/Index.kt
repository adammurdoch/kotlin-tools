package net.rubygrapefruit.store

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class Index(
    private val index: RegularFile,
    private val codec: SimpleCodec
) {
    fun <T> query(query: (Map<String, Long>) -> T): T {
        val index = readIndex()
        return query(index)
    }

    fun update(update: (MutableMap<String, Long>) -> Unit) {
        val index = readIndex()
        update(index)
        this.index.withContent { content ->
            val encoder = codec.encoder(content.writeStream)
            encoder.ushort(version)
            encoder.ushort(codec.version)
            encoder.string(Json.encodeToString(serializer(), index))
        }
    }

    fun accept(visitor: ContentVisitor) {
        query { index ->
            for (entry in index.entries.sortedBy { it.key }) {
                visitor.value(entry.key, ContentVisitor.ValueInfo(entry.value))
            }
        }
    }

    private fun readIndex(): MutableMap<String, Long> {
        return index.withContent { content ->
            if (content.length() == 0L) {
                mutableMapOf()
            } else {
                val decoder = codec.decoder(content.readStream)
                require(decoder.ushort() == version)
                require(decoder.ushort() == codec.version)
                Json.decodeFromString<MutableMap<String, Long>>(decoder.string())
            }
        }.get()
    }
}