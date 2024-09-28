package net.rubygrapefruit.io.codec

import kotlinx.io.Sink
import kotlinx.io.Source

class SimpleCodec {
    val version: UShort
        get() = 1u

    fun encoder(sink: Sink): Encoder = SimpleEncoder(sink)

    fun decoder(source: Source): Decoder = SimpleDecoder(source)
}