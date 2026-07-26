package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Position
import kotlin.test.*

class BufferingCharStreamTest {
    @Test
    fun `can query empty stream`() {
        val stream = BufferingCharStream()

        assertEquals(0, stream.available)
        assertFalse(stream.finished)

        assertEquals("", stream.get(0, 0))
    }

    @Test
    fun `can append and read fewer than buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123")

        assertEquals(3, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('3', stream.get(2))

        assertEquals("123", stream.get(0, 3))
    }

    @Test
    fun `can append buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1234")

        assertEquals(4, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('4', stream.get(3))

        assertEquals("1234", stream.get(0, 4))
    }

    @Test
    fun `can append and read more than buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123456")

        assertEquals(6, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('6', stream.get(5))

        assertEquals("123456", stream.get(0, 6))
    }

    @Test
    fun `can append and read two buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12345678")

        assertEquals(8, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('8', stream.get(7))

        assertEquals("12345678", stream.get(0, 8))
    }

    @Test
    fun `can append and read multiple buffer len chars`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123456789abc")

        assertEquals(12, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('6', stream.get(5))
        assertEquals('c', stream.get(11))

        assertEquals("123456789abc", stream.get(0, 12))
    }

    @Test
    fun `can append chars to fill buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123")
        stream.append("4")

        assertEquals(4, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('4', stream.get(3))
    }

    @Test
    fun `can append chars to fill then overflow buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123")
        stream.append("4")
        stream.append("5")

        assertEquals(5, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('5', stream.get(4))
    }

    @Test
    fun `can append chars to overflow buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123")
        stream.append("45678")

        assertEquals(8, stream.available)

        assertEquals('1', stream.get(0))
        assertEquals('8', stream.get(7))
    }

    @Test
    fun `can append zero chars to empty stream`() {
        val stream = BufferingCharStream()

        stream.append("")

        assertEquals(0, stream.available)
    }

    @Test
    fun `can append zero chars to full buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1234")
        stream.append("")

        assertEquals(4, stream.available)
    }

    @Test
    fun `can query empty slice`() {
        val stream = BufferingCharStream()

        stream.append("123")

        assertEquals("", stream.get(1, 1))
    }

    @Test
    fun `can query slice from last buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12345678")

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

        stream.append("12345678")

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

        stream.append("12345678")

        assertEquals("3456", stream.get(2, 6))
        assertEquals("12345", stream.get(0, 5))
        assertEquals("345678", stream.get(2, 8))
    }

    @Test
    fun `can query context from empty stream`() {
        val stream = BufferingCharStream()

        stream.contextAt(0).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(0).apply {
            assertNotNull(this)
            assertEquals(Position.Zero, position.position)
            assertEquals(1, position.line)
            assertEquals(1, position.col)
            assertEquals("", lineText)
        }
    }

    @Test
    fun `can query context from line of last buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1\n23\n45\n")

        stream.contextAt(5).apply {
            assertNotNull(this)
            assertEquals(Position(5), position.position)
            assertEquals(3, position.line)
            assertEquals(1, position.col)
            assertEquals("45", lineText)
        }

        stream.contextAt(6).apply {
            assertNotNull(this)
            assertEquals(Position(6), position.position)
            assertEquals(3, position.line)
            assertEquals(2, position.col)
            assertEquals("45", lineText)
        }
    }

    @Test
    fun `can query context from last line of last buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1\n2\n\n456")

        stream.contextAt(5).apply {
            assertNull(this)
        }
        stream.contextAt(7).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(5).apply {
            assertNotNull(this)
            assertEquals(Position(5), position.position)
            assertEquals(4, position.line)
            assertEquals(1, position.col)
            assertEquals("456", lineText)
        }

        stream.contextAt(7).apply {
            assertNotNull(this)
            assertEquals(Position(7), position.position)
            assertEquals(4, position.line)
            assertEquals(3, position.col)
            assertEquals("456", lineText)
        }
    }

    @Test
    fun `can query context from line of previous buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("\n12\n123")

        stream.contextAt(1).apply {
            assertNotNull(this)
            assertEquals(Position(1), position.position)
            assertEquals(2, position.line)
            assertEquals(1, position.col)
            assertEquals("12", lineText)
        }
        stream.contextAt(2).apply {
            assertNotNull(this)
            assertEquals(Position(2), position.position)
            assertEquals(2, position.line)
            assertEquals(2, position.col)
            assertEquals("12", lineText)
        }
    }

    @Test
    fun `can query context from last line that spans multiple buffers`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("\n1\n23456789")

        stream.contextAt(3).apply {
            assertNull(this)
        }
        stream.contextAt(7).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(3).apply {
            assertNotNull(this)
            assertEquals(Position(3), position.position)
            assertEquals(3, position.line)
            assertEquals(1, position.col)
            assertEquals("23456789", lineText)
        }
        stream.contextAt(7).apply {
            assertNotNull(this)
            assertEquals(Position(7), position.position)
            assertEquals(3, position.line)
            assertEquals(5, position.col)
            assertEquals("23456789", lineText)
        }
        stream.contextAt(10).apply {
            assertNotNull(this)
            assertEquals(Position(10), position.position)
            assertEquals(3, position.line)
            assertEquals(8, position.col)
            assertEquals("23456789", lineText)
        }
    }

    @Test
    fun `can query context from first line that spans multiple buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123456789\na")

        stream.contextAt(0).apply {
            assertNotNull(this)
            assertEquals(Position.Zero, position.position)
            assertEquals(1, position.line)
            assertEquals(1, position.col)
            assertEquals("123456789", lineText)
        }
        stream.contextAt(2).apply {
            assertNotNull(this)
            assertEquals(Position(2), position.position)
            assertEquals(1, position.line)
            assertEquals(3, position.col)
            assertEquals("123456789", lineText)
        }
        stream.contextAt(5).apply {
            assertNotNull(this)
            assertEquals(Position(5), position.position)
            assertEquals(1, position.line)
            assertEquals(6, position.col)
            assertEquals("123456789", lineText)
        }
        stream.contextAt(8).apply {
            assertNotNull(this)
            assertEquals(Position(8), position.position)
            assertEquals(1, position.line)
            assertEquals(9, position.col)
            assertEquals("123456789", lineText)
        }
    }

    @Test
    fun `can query context at end of line`() {
        val stream = BufferingCharStream()

        stream.append("1\n2\n\n")

        stream.contextAt(1).apply {
            assertNotNull(this)
            assertEquals(Position(1), position.position)
            assertEquals(1, position.line)
            assertEquals(2, position.col)
            assertEquals("1", lineText)
        }

        stream.contextAt(4).apply {
            assertNotNull(this)
            assertEquals(Position(4), position.position)
            assertEquals(3, position.line)
            assertEquals(1, position.col)
            assertEquals("", lineText)
        }
    }

    @Test
    fun `can query context at start of input`() {
        val stream = BufferingCharStream()

        stream.append("123")

        stream.contextAt(0).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(0).apply {
            assertNotNull(this)
            assertEquals(Position(0), position.position)
            assertEquals(1, position.line)
            assertEquals(1, position.col)
            assertEquals("123", lineText)
        }
    }

    @Test
    fun `can query context at end of input`() {
        val stream = BufferingCharStream()

        stream.append("1\n2\n34")

        stream.contextAt(6).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(6).apply {
            assertNotNull(this)
            assertEquals(Position(6), position.position)
            assertEquals(3, position.line)
            assertEquals(3, position.col)
            assertEquals("34", lineText)
        }
    }

    @Test
    fun `can append fewer than buffer len chars from reader function`() {
        val stream = BufferingCharStream(bufferLen = 4)

        val nread = stream.appendFrom { buffer, offset, max ->
            assertEquals(4, max)
            assertEquals(0, offset)
            "123".toCharArray().copyInto(buffer, offset, 0, 3)
            3
        }

        assertEquals(3, nread)
        assertEquals(3, stream.available)
        assertEquals("123", stream.get(0, 3))
    }

    @Test
    fun `can append from reader function to fill buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("12")
        val nread = stream.appendFrom { buffer, offset, max ->
            assertEquals(2, max)
            assertEquals(2, offset)
            "34".toCharArray().copyInto(buffer, offset, 0, 2)
            2
        }

        assertEquals(2, nread)
        assertEquals(4, stream.available)
        assertEquals("1234", stream.get(0, 4))
    }

    @Test
    fun `can query context appended using reader function`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.appendFrom { buffer, offset, max ->
            assertEquals(4, max)
            "12\n3".toCharArray().copyInto(buffer, offset, 0, 4)
            4
        }
        stream.appendFrom { buffer, offset, max ->
            assertEquals(4, max)
            "4\n5".toCharArray().copyInto(buffer, offset, 0, 3)
            3
        }
        stream.end()

        stream.contextAt(4).apply {
            assertNotNull(this)
            assertEquals(Position(4), position.position)
            assertEquals(2, position.line)
            assertEquals(2, position.col)
            assertEquals("34", lineText)
        }
    }

    private fun BufferingCharStream.contextAt(position: Int) = contextAt(Position(position))

    private fun BufferingCharStream.append(text: String) = append(text.toCharArray())
}