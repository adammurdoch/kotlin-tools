package net.rubygrapefruit.store

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.Decoder
import net.rubygrapefruit.io.codec.Encoder
import net.rubygrapefruit.io.codec.SimpleCodec

internal const val version: UShort = 1u

internal fun Encoder.fileHeader(codec: SimpleCodec) {
    ushort(version)
    ushort(codec.version)
}

internal fun Decoder.checkFileHeader(codec: SimpleCodec, file: RegularFile) {
    val value1 = ushort()
    if (value1 != version) {
        throw IllegalStateException("Unexpected file format version in file ${file.absolutePath}. Found $value1, expected $version")
    }
    val value2 = ushort()
    if (value2 != codec.version) {
        throw IllegalStateException("Unexpected codec version in file ${file.absolutePath}. Found $value1, expected ${codec.version}")
    }
}

internal fun unrecognizedFormat(directory: Directory): Nothing {
    throw IllegalStateException("Unexpected content found in directory ${directory.absolutePath}")
}