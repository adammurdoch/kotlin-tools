@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.FileContent
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class Index(
    index: RegularFile,
    private val codec: SimpleCodec
) : AutoCloseable {
    private val contentResource = index.openContent().successful()
    private val currentIndex = contentResource.using { readIndex(it, codec) }

    override fun close() {
        contentResource.close()
    }

    fun <T> query(query: (Map<String, Long>) -> T): T {
        return query(currentIndex)
    }

    fun update(update: (MutableMap<String, Long>) -> Unit) {
        update(currentIndex)
        contentResource.using { content ->
            content.seek(0)
            val encoder = codec.encoder(content.writeStream)
            encoder.ushort(version)
            encoder.ushort(codec.version)
            encoder.string(Json.encodeToString(serializer(), currentIndex))
        }
    }

    fun accept(visitor: ContentVisitor) {
        query { index ->
            for (entry in index.entries.sortedBy { it.key }) {
                visitor.value(entry.key, ContentVisitor.ValueInfo(entry.value))
            }
        }
    }

    private fun readIndex(content: FileContent, codec: SimpleCodec): MutableMap<String, Long> {
        return if (content.length() == 0L) {
            mutableMapOf()
        } else {
            content.seek(0)
            val decoder = codec.decoder(content.readStream)
            require(decoder.ushort() == version)
            require(decoder.ushort() == codec.version)
            Json.decodeFromString<MutableMap<String, Long>>(decoder.string())
        }
    }
}