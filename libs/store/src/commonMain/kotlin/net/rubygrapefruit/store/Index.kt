package net.rubygrapefruit.store

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleDecoder
import net.rubygrapefruit.io.codec.SimpleEncoder

internal class Index(
    private val index: RegularFile
) {
    fun <T> query(query: (Map<String, UInt>) -> T): T {
        val index = readIndex()
        return query(index)
    }

    fun update(update: (MutableMap<String, UInt>) -> Unit) {
        val index = readIndex()
        update(index)
        this.index.withContent { content ->
            val encoder = SimpleEncoder(content.writeStream)
            encoder.ushort(version)
            encoder.string(Json.encodeToString(serializer(), index))
        }
    }

    fun accept(visitor: ContentVisitor) {
        query { index ->
            for (entry in index.entries.sortedBy { it.key }) {
                visitor.value(entry.key, ContentVisitor.ValueInfo())
            }
        }
    }

    private fun readIndex(): MutableMap<String, UInt> {
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