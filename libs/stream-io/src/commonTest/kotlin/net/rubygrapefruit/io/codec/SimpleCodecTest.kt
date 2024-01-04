package net.rubygrapefruit.io.codec

import net.rubygrapefruit.io.stream.ByteArrayReadStream
import net.rubygrapefruit.io.stream.CollectingWriteStream
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleCodecTest {
    @Test
    fun `encodes and decodes UShort`() {
        val writeStream = CollectingWriteStream()
        val encoder = SimpleEncoder(writeStream)

        encoder.ushort(1u)

        val decoder = SimpleDecoder(ByteArrayReadStream(writeStream.toByteArray()))

        assertEquals(1u, decoder.ushort())
    }

    @Test
    fun `encodes and decodes String`() {
        val writeStream = CollectingWriteStream()
        val encoder = SimpleEncoder(writeStream)

        encoder.string("123")

        val decoder = SimpleDecoder(ByteArrayReadStream(writeStream.toByteArray()))

        assertEquals("123", decoder.string())
    }
}