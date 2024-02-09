@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

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

    fun <T> query(query: (Map<String, Address>) -> T): T {
        return query(currentIndex)
    }

    fun update(update: (MutableMap<String, Address>) -> Unit) {
        update(currentIndex)
        contentResource.using { content ->
            content.seek(0)
            val encoder = codec.encoder(content.writeStream)
            encoder.ushort(version)
            encoder.ushort(codec.version)
            encoder.int(currentIndex.size)
            for (entry in currentIndex.entries) {
                encoder.string(entry.key)
                encoder.long(entry.value.offset)
            }
        }
    }

    fun accept(visitor: ContentVisitor) {
        query { index ->
            for (entry in index.entries.sortedBy { it.key }) {
                visitor.value(entry.key, ContentVisitor.ValueInfo(entry.value))
            }
        }
    }

    private fun readIndex(content: FileContent, codec: SimpleCodec): MutableMap<String, Address> {
        return if (content.length() == 0L) {
            mutableMapOf()
        } else {
            content.seek(0)
            val decoder = codec.decoder(content.readStream)
            require(decoder.ushort() == version)
            require(decoder.ushort() == codec.version)
            val count = decoder.int()
            val result = LinkedHashMap<String, Address>(count)
            for (i in 0 until count) {
                val key = decoder.string()
                val address = Address(decoder.long())
                result[key] = address
            }
            result
        }
    }
}