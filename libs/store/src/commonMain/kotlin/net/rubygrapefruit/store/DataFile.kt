@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec
import kotlin.math.min

internal class DataFile(
    file: RegularFile,
    private val codec: SimpleCodec
) : AutoCloseable {
    private val readContent = file.openContent().successful()
    private val writeContent = file.openContent().successful()

    init {
        writeContent.using { content ->
            content.seekToEnd()
        }
    }

    override fun close() {
        readContent.close()
        writeContent.close()
    }

    fun <T> read(block: Block, serializer: DeserializationStrategy<T>): T {
        return readContent.using { content ->
            content.seek(block.address.offset)
            val decoder = codec.decoder(content.readStream)
            Json.decodeFromString(serializer, decoder.string())
        }
    }

    fun <T> append(value: T, serializer: SerializationStrategy<T>): Block {
        return writeContent.using { content ->
            val pos = content.currentPosition
            val encoder = codec.encoder(content.writeStream)
            encoder.string(Json.encodeToString(serializer, value))
            val size = content.currentPosition - pos
            Block(Address(pos), Size(size))
        }
    }

    fun copyFrom(sourceFile: DataFile, block: Block): Block {
        return writeContent.using { dest ->
            sourceFile.readContent.using { source ->
                val pos = dest.currentPosition
                val buffer = ByteArray(16 * 1024)
                source.seek(block.address.offset)
                var remaining = block.size.value
                while (remaining > 0) {
                    val count = min(remaining, buffer.size)
                    source.readStream.readFully(buffer, 0, count)
                    dest.writeStream.write(buffer, 0, count)
                    remaining -= count
                }
                Block(Address(pos), block.size)
            }
        }
    }
}