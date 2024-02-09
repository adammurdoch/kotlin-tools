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
    private val fileContent = file.openContent().successful()

    override fun close() {
        fileContent.close()
    }

    fun <T> read(address: Address, serializer: DeserializationStrategy<T>): T {
        return fileContent.using { content ->
            content.seek(address.offset)
            val decoder = codec.decoder(content.readStream)
            Json.decodeFromString(serializer, decoder.string())
        }
    }

    fun <T> write(value: T, serializer: SerializationStrategy<T>): Address {
        return fileContent.using { content ->
            val pos = content.seekToEnd()
            val encoder = codec.encoder(content.writeStream)
            encoder.string(Json.encodeToString(serializer, value))
            Address(pos)
        }
    }
}