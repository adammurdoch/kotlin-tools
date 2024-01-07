package net.rubygrapefruit.io.codec

import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.WriteStream

class SimpleCodec {
    val version: UShort
        get() = 1u

    fun encoder(stream: WriteStream): Encoder = SimpleEncoder(stream)

    fun decoder(stream: ReadStream): Decoder = SimpleDecoder(stream)
}