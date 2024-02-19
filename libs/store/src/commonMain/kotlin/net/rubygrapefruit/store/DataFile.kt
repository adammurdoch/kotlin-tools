@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class DataFile(
    file: RegularFile,
    private val codec: SimpleCodec
) : AutoCloseable {
    private val readContent = file.openContent().successful()
    private val writeContent = file.openContent().successful()

    init {
        readContent.using { content ->
            if (content.length() > 0) {
                val decoder = codec.decoder(content.readStream)
                decoder.checkFileHeader(codec, file)
            }
        }
        writeContent.using { content ->
            if (content.length() == 0L) {
                val encoder = codec.encoder(content.writeStream)
                encoder.fileHeader(codec)
            } else {
                content.seekToEnd()
            }
        }
    }

    override fun close() {
        readContent.close()
        writeContent.close()
    }

    fun <T> read(address: Address, serializer: DeserializationStrategy<T>): T {
        return readContent.using { content ->
            content.seek(address.offset)
            val decoder = codec.decoder(content.readStream)
            Json.decodeFromString(serializer, decoder.string())
        }
    }

    fun <T> write(value: T, serializer: SerializationStrategy<T>): Address {
        return writeContent.using { content ->
            val pos = content.currentPosition
            val encoder = codec.encoder(content.writeStream)
            encoder.string(Json.encodeToString(serializer, value))
            Address(pos)
        }
    }
}