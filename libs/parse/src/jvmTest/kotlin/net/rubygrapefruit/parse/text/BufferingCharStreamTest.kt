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

        assertEquals("", stream.get(0, 0))
    }

    @Test
    fun `can append zero chars`() {
        val stream = BufferingCharStream()

        stream.append("1".toCharArray())
        stream.append("".toCharArray())

        assertEquals(1, stream.available)
    }

    @Test
    fun `can append and read fewer than buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())

        assertEquals(3, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('3', stream.get(2))

        assertEquals("123", stream.get(0, 3))
    }

    @Test
    fun `can append buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1234".toCharArray())

        assertEquals(4, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('4', stream.get(3))

        assertEquals("1234", stream.get(0, 4))
    }

    @Test
    fun `can append and read more than buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123456".toCharArray())

        assertEquals(6, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('6', stream.get(5))

        assertEquals("123456", stream.get(0, 6))
    }

    @Test
    fun `can append and read two buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12345678".toCharArray())

        assertEquals(8, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('8', stream.get(7))

        assertEquals("12345678", stream.get(0, 8))
    }

    @Test
    fun `can append and read multiple buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123456789abc".toCharArray())

        assertEquals(12, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('6', stream.get(5))
        assertEquals('c', stream.get(11))

        assertEquals("123456789abc", stream.get(0, 12))
    }

    @Test
    fun `can append chars to fill buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())
        stream.append("4".toCharArray())

        assertEquals(4, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('4', stream.get(3))
    }

    @Test
    fun `can append chars to fill then overflow buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())
        stream.append("4".toCharArray())
        stream.append("5".toCharArray())

        assertEquals(5, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('5', stream.get(4))
    }

    @Test
    fun `can append chars to overflow buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123".toCharArray())
        stream.append("45678".toCharArray())

        assertEquals(8, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('8', stream.get(7))
    }

    @Test
    fun `can query empty slice`() {
        val stream = BufferingCharStream()

        stream.append("123".toCharArray())

        assertEquals("", stream.get(1, 1))
    }

    @Test
    fun `can query slice from last buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12345678".toCharArray())

        assertEquals("", stream.get(5, 5))
        assertEquals("6", stream.get(5, 6))
        assertEquals("56", stream.get(4, 6))
        assertEquals("67", stream.get(5, 7))
        assertEquals("78", stream.get(6, 8))
        assertEquals("5678", stream.get(4, 8))
    }

    @Test
    fun `can query slice from previous buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12345678".toCharArray())

        assertEquals("", stream.get(2, 2))
        assertEquals("2", stream.get(1, 2))
        assertEquals("12", stream.get(0, 2))
        assertEquals("23", stream.get(1, 3))
        assertEquals("34", stream.get(2, 4))
        assertEquals("1234", stream.get(0, 4))
    }

    @Test
    fun `can query slice from multiple buffers`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12345678".toCharArray())

        assertEquals("3456", stream.get(2, 6))
        assertEquals("12345", stream.get(0, 5))
        assertEquals("345678", stream.get(2, 8))
    }
}