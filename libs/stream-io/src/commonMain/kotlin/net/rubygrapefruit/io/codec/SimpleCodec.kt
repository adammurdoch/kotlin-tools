package net.rubygrapefruit.io.codec

import kotlinx.io.RawSink
import kotlinx.io.RawSource

class SimpleCodec {
    val version: UShort
        get() = 1u

    fun encoder(sink: RawSink): Encoder = SimpleEncoder(sink)

    fun decoder(source: RawSource): Decoder = SimpleDecoder(source)
}