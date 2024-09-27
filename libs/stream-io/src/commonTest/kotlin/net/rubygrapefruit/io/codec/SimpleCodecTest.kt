@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.io.codec

import net.rubygrapefruit.io.stream.ByteArrayReadStream
import net.rubygrapefruit.io.stream.CollectingRawSink
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleCodecTest {
    @Test
    fun `encodes and decodes UByte`() {
        for (value in listOf(0u, 0x1u, 0x10u, 0xffu, UByte.MAX_VALUE)) {
            assertEquals(value, encodeAndDecode(value, Encoder::ubyte, Decoder::ubyte))
        }
    }

    @Test
    fun `encodes and decodes UShort`() {
        for (value in listOf(0u, 0x1u, 0xffu, 0x100u, 0xff01u, 0x1122u, UShort.MAX_VALUE)) {
            assertEquals(value, encodeAndDecode(value, Encoder::ushort, Decoder::ushort))
        }
    }

    @Test
    fun `encodes and decodes Int`() {
        for (value in listOf(0, 1, -1, 0xff, 0xff01, 0xff0001, 0x7f000001, 0x11223344, Int.MAX_VALUE, Int.MIN_VALUE)) {
            assertEquals(value, encodeAndDecode(value, Encoder::int, Decoder::int))
        }
    }

    @Test
    fun `encodes and decodes Long`() {
        for (value in listOf(0, 1, -1, 0xff, 0xff01, 0xff0001, 0xff000001, 0xff000000000001, 0x7f00000000000000, 0x1122334455667788, Long.MAX_VALUE, Long.MIN_VALUE)) {
            assertEquals(value, encodeAndDecode(value, Encoder::long, Decoder::long))
        }
    }

    @Test
    fun `encodes and decodes String`() {
        for (value in listOf("", "123", "日本ご")) {
            assertEquals(value, encodeAndDecode(value, Encoder::string, Decoder::string))
        }
    }

    private fun <T> encodeAndDecode(value: T, encode: (Encoder, T) -> Unit, decode: (Decoder) -> T): T {
        val sink = CollectingRawSink()
        val codec = SimpleCodec()
        val encoder = codec.encoder(sink)

        println("encode: ${value.dump()}")

        encode(encoder, value)

        val decoder = codec.decoder(ByteArrayReadStream(sink.toByteArray()))

        return decode(decoder).also {
            println("decoded: ${it.dump()}")
        }
    }

    fun Any?.dump(): String {
        return when (this) {
            is UShort -> "${this.toHexString(HexFormat.UpperCase)} (UShort)"
            is Int -> "${this.toHexString(HexFormat.UpperCase)} (Int)"
            is String -> "'$this'"
            else -> toString()
        }
    }
}