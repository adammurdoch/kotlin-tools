package net.rubygrapefruit.io.codec

import kotlinx.io.RawSink
import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.WriteStream

class SimpleCodec {
    val version: UShort
        get() = 1u

    fun encoder(sink: RawSink): Encoder = SimpleEncoder(sink)

    fun decoder(stream: ReadStream): Decoder = SimpleDecoder(stream)
}