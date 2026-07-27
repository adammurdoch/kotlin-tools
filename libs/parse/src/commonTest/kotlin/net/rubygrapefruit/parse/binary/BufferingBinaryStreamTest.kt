package net.rubygrapefruit.parse.binary

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BufferingBinaryStreamTest {
    @Test
    fun `can append fewer than buffer len bytes using reader function`() {
        val stream = BufferingByteStream(bufferLen = 4)

        val nread = stream.appendFrom { buffer, offset, max ->
            assertEquals(4, max)
            assertEquals(0, offset)
            byteArrayOf(0x1, 0x2).copyInto(buffer, 0, 0, 2)
            2
        }

        assertEquals(2, nread)

        assertEquals(2, stream.available)
        assertContentEquals(byteArrayOf(0x1, 0x2), stream.get(0, 2))
    }

    @Test
    fun `can append bytes to fill buffer using reader function`() {
        val stream = BufferingByteStream(bufferLen = 4)

        stream.append(byteArrayOf(0x1, 0x2))
        val nread = stream.appendFrom { buffer, offset, max ->
            assertEquals(2, max)
            assertEquals(2, offset)
            byteArrayOf(0x3, 0x4).copyInto(buffer, 2, 0, 2)
            2
        }

        assertEquals(2, nread)

        assertEquals(4, stream.available)
        assertContentEquals(byteArrayOf(0x1, 0x2, 0x3, 0x4), stream.get(0, 4))
    }

    @Test
    fun `can append fewer than buffer len bytes to full buffer using reader function`() {
        val stream = BufferingByteStream(bufferLen = 4)

        stream.append(byteArrayOf(0x1, 0x2, 0x3, 0x4))
        val nread = stream.appendFrom { buffer, offset, max ->
            assertEquals(4, max)
            assertEquals(0, offset)
            byteArrayOf(0x5, 0x6).copyInto(buffer, 0, 0, 2)
            2
        }

        assertEquals(2, nread)

        assertEquals(6, stream.available)
        assertContentEquals(byteArrayOf(0x1, 0x2, 0x3, 0x4, 0x5, 0x6), stream.get(0, 6))
    }
}