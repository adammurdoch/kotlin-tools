@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.io.codec

import net.rubygrapefruit.io.stream.ByteArrayReadStream
import net.rubygrapefruit.io.stream.CollectingWriteStream
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleCodecTest {
    @Test
    fun `encodes and decodes UShort`() {
        for (value in listOf(0u, 0x1u, 0xffu, 0x100u, 0xff01u, UShort.MAX_VALUE)) {
            assertEquals(value, encodeAndDecode(value, Encoder::ushort, Decoder::ushort))
        }
    }

    @Test
    fun `encodes and decodes Int`() {
        for (value in listOf(0, 1, -1, 0xff, 0xff01, 0xff0001, 0x7f000001, Int.MAX_VALUE, Int.MIN_VALUE)) {
            assertEquals(value, encodeAndDecode(value, Encoder::int, Decoder::int))
        }
    }

    @Test
    fun `encodes and decodes String`() {
        for (value in listOf("", "123", "日本ご")) {
            assertEquals(value, encodeAndDecode(value, Encoder::string, Decoder::string))
        }
    }

    private fun <T> encodeAndDecode(value: T, encode: (Encoder, T) -> Unit, decode: (Decoder) -> T): T {
        val writeStream = CollectingWriteStream()
        val encoder = SimpleEncoder(writeStream)

        println("-> ENCODE ${value.dump()}")

        encode(encoder, value)

        val decoder = SimpleDecoder(ByteArrayReadStream(writeStream.toByteArray()))

        return decode(decoder).also {
            println("-> DECODED ${it.dump()}")
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