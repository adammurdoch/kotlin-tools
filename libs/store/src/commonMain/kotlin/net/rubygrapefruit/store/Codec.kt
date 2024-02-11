package net.rubygrapefruit.store

import net.rubygrapefruit.io.codec.Decoder
import net.rubygrapefruit.io.codec.Encoder
import net.rubygrapefruit.io.codec.SimpleCodec

internal fun Encoder.fileHeader(codec: SimpleCodec) {
    ushort(version)
    ushort(codec.version)
}

internal fun Decoder.checkFileHeader(codec: SimpleCodec) {
    val value1 = ushort()
    if (value1 != version) {
        throw IllegalStateException("Unexpected version in file.")
    }
    val value2 = ushort()
    if (value2 != codec.version) {
        throw IllegalStateException("Unexpected version in file.")
    }
}