package net.rubygrapefruit.store

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class MetadataFile(
    private val metadataFile: RegularFile,
    private val codec: SimpleCodec
) {
    private var generation: Int = 0

    val currentGeneration: Int
        get() = generation

    init {
        metadataFile.withContent { content ->
            val length = content.length()
            if (length == 0L) {
                storeGeneration()
            } else {
                val decoder = codec.decoder(content.readStream)
                decoder.checkFileHeader(codec, metadataFile)
                generation = decoder.int()
            }
        }
    }

    fun updateGeneration(newGeneration: Int) {
        generation = newGeneration
        storeGeneration()
    }

    private fun storeGeneration() {
        metadataFile.withContent { content ->
            val encoder = codec.encoder(content.writeStream)
            encoder.fileHeader(codec)
            encoder.int(generation)
        }
    }
}