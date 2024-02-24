package net.rubygrapefruit.store

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class LogFile(
    private val file: RegularFile,
    private val codec: SimpleCodec
) : StoreFile() {
    private val writeContent = file.openContent().successful()

    init {
        writeContent.using { content ->
            content.seekToEnd()
        }
    }

    override fun close() {
        writeContent.close()
    }

    override fun closeAndDelete() {
        close()
        file.delete()
    }

    fun append(change: StoreChange) {
        writeContent.using { content ->
            val encoder = codec.encoder(content.writeStream)
            encoder.encode(change)
        }
    }

    fun read(visitor: (StoreChange) -> Unit) {
        file.withContent { content ->
            val length = content.length()
            val decoder = codec.decoder(content.readStream)
            while (content.currentPosition != length) {
                val change = decoder.decode()
                visitor(change)
            }
        }
    }
}