package net.rubygrapefruit.parse.text

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class BufferingCharStreamTest {
    @Test
    fun `can query empty stream`() {
        val stream = BufferingCharStream()

        assertEquals(0, stream.available)
        assertFalse(stream.finished)
    }

    @Test
    fun `can append and read fewer than buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())

        assertEquals(3, stream.available)
    }

    @Test
    fun `can append buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1234".toCharArray())

        assertEquals(4, stream.available)
    }

    @Test
    fun `can append and read more than buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123456".toCharArray())

        assertEquals(6, stream.available)
    }

    @Test
    fun `can append and read more than multiple buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12345678".toCharArray())

        assertEquals(8, stream.available)
    }

    @Test
    fun `can append chars to fill buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())
        stream.append("4".toCharArray())

        assertEquals(4, stream.available)
    }

    @Test
    fun `can append chars to fill then overflow buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())
        stream.append("4".toCharArray())
        stream.append("5".toCharArray())

        assertEquals(5, stream.available)
    }

    @Test
    fun `can append chars to overflow buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())
        stream.append("45678".toCharArray())

        assertEquals(8, stream.available)
    }
}