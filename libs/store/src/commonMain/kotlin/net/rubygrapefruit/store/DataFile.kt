package net.rubygrapefruit.store

import kotlinx.io.buffered
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class DataFile(
    private val file: RegularFile,
    private val codec: SimpleCodec
) : StoreFile() {
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

    override fun closeAndDelete() {
        close()
        file.delete()
    }

    fun <T> read(block: Block, serializer: DeserializationStrategy<T>): T {
        return readContent.using { content ->
            content.seek(block.address.offset)
            val decoder = codec.decoder(content.source.buffered())
            Json.decodeFromString(serializer, decoder.string())
        }
    }

    fun <T> append(value: T, serializer: SerializationStrategy<T>): Block {
        return writeContent.using { content ->
            val pos = content.currentPosition
            content.write { sink ->
                val encoder = codec.encoder(sink)
                encoder.string(Json.encodeToString(serializer, value))
            }
            val size = content.currentPosition - pos
            Block(Address(pos), Size(size))
        }
    }

    fun copyFrom(sourceFile: DataFile, block: Block): Block {
        return writeContent.using { dest ->
            sourceFile.readContent.using { source ->
                val pos = dest.currentPosition
                source.seek(block.address.offset)
                val bufferedSink = dest.sink.buffered()
                bufferedSink.write(source.source, block.size.value.toLong())
                bufferedSink.flush()
                Block(Address(pos), block.size)
            }
        }
    }
}