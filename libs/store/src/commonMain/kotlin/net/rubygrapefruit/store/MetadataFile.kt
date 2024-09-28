package net.rubygrapefruit.store

import kotlinx.io.buffered
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class MetadataFile(
    private val metadataFile: RegularFile,
    private val codec: SimpleCodec
) {
    private var metadata = Metadata(1, 0, 0)

    val currentGeneration: Int
        get() = metadata.generation

    val compactedChanges: Int
        get() = metadata.compactedChanges

    init {
        metadataFile.withContent { content ->
            val length = content.length()
            if (length == 0L) {
                storeMetadata()
            } else {
                val decoder = codec.decoder(content.source.buffered())
                decoder.checkFileHeader(codec, metadataFile)
                metadata = Metadata(decoder.int(), decoder.int(), decoder.int())
            }
        }
    }

    fun updateGeneration(newGeneration: Int, compactedChanges: Int) {
        metadata = Metadata(newGeneration, compactedChanges, 0)
        storeMetadata()
    }

    fun updateNonCompactedChanges(changes: Int) {
        metadata = Metadata(metadata.generation, metadata.compactedChanges, changes)
        storeMetadata()
    }

    private fun storeMetadata() {
        metadataFile.withContent { content ->
            content.write { sink ->
                val encoder = codec.encoder(sink)
                encoder.fileHeader(codec)
                encoder.int(metadata.generation)
                encoder.int(metadata.compactedChanges)
                encoder.int(metadata.nonCompactedChanges)
            }
        }
    }
}