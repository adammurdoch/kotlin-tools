package net.rubygrapefruit.store

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class MetadataFile(
    metadataFile: RegularFile,
    codec: SimpleCodec
) {
    init {
        metadataFile.withContent { content ->
            val length = content.length()
            if (length == 0L) {
                val encoder = codec.encoder(content.writeStream)
                encoder.fileHeader(codec)
            } else {
                val decoder = codec.decoder(content.readStream)
                decoder.checkFileHeader(codec, metadataFile)
            }
        }
    }
}