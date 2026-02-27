package net.rubygrapefruit.parse.text

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

    @Test
    fun `can query position from empty stream`() {
        val stream = BufferingCharStream()

        val pos = stream.posAt(0)
        assertEquals(0, pos.offset)
        assertEquals(1, pos.line)
        assertEquals(1, pos.col)
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
            assertEquals(0, position.offset)
            assertEquals(1, position.line)
            assertEquals(1, position.col)
            assertEquals("", lineText)
        }
    }

    @Test
    fun `can query position from start of stream`() {
        val stream = BufferingCharStream()

        stream.append("123".toCharArray())

        val pos = stream.posAt(0)
        assertEquals(0, pos.offset)
        assertEquals(1, pos.line)
        assertEquals(1, pos.col)
    }

    @Test
    fun `can query position from last buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1\n2\n\n456".toCharArray())

        stream.posAt(5).apply {
            assertEquals(5, offset)
            assertEquals(4, line)
            assertEquals(1, col)
        }

        stream.posAt(7).apply {
            assertEquals(7, offset)
            assertEquals(4, line)
            assertEquals(3, col)
        }
    }

    @Test
    fun `can query context from line of last buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1\n23\n45\n".toCharArray())

        stream.contextAt(5).apply {
            assertNotNull(this)
            assertEquals(5, position.offset)
            assertEquals(3, position.line)
            assertEquals(1, position.col)
            assertEquals("45", lineText)
        }

        stream.contextAt(6).apply {
            assertNotNull(this)
            assertEquals(6, position.offset)
            assertEquals(3, position.line)
            assertEquals(2, position.col)
            assertEquals("45", lineText)
        }
    }

    @Test
    fun `can query context from last line of last buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1\n2\n\n456".toCharArray())

        stream.contextAt(5).apply {
            assertNull(this)
        }
        stream.contextAt(7).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(5).apply {
            assertNotNull(this)
            assertEquals(5, position.offset)
            assertEquals(4, position.line)
            assertEquals(1, position.col)
            assertEquals("456", lineText)
        }

        stream.contextAt(7).apply {
            assertNotNull(this)
            assertEquals(7, position.offset)
            assertEquals(4, position.line)
            assertEquals(3, position.col)
            assertEquals("456", lineText)
        }
    }

    @Test
    fun `can query position from previous buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("1\n23456".toCharArray())

        stream.posAt(2).apply {
            assertEquals(2, offset)
            assertEquals(2, line)
            assertEquals(1, col)
        }

        stream.posAt(3).apply {
            assertEquals(3, offset)
            assertEquals(2, line)
            assertEquals(2, col)
        }
    }

    @Test
    fun `can query context from line of previous buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("\n12\n123".toCharArray())

        stream.contextAt(1).apply {
            assertNotNull(this)
            assertEquals(1, position.offset)
            assertEquals(2, position.line)
            assertEquals(1, position.col)
            assertEquals("12", lineText)
        }
        stream.contextAt(2).apply {
            assertNotNull(this)
            assertEquals(2, position.offset)
            assertEquals(2, position.line)
            assertEquals(2, position.col)
            assertEquals("12", lineText)
        }
    }

    @Test
    fun `can query context from last line that spans multiple buffers`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("\n1\n23456789".toCharArray())

        stream.contextAt(3).apply {
            assertNull(this)
        }
        stream.contextAt(7).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(3).apply {
            assertNotNull(this)
            assertEquals(3, position.offset)
            assertEquals(3, position.line)
            assertEquals(1, position.col)
            assertEquals("23456789", lineText)
        }
        stream.contextAt(7).apply {
            assertNotNull(this)
            assertEquals(7, position.offset)
            assertEquals(3, position.line)
            assertEquals(5, position.col)
            assertEquals("23456789", lineText)
        }
        stream.contextAt(10).apply {
            assertNotNull(this)
            assertEquals(10, position.offset)
            assertEquals(3, position.line)
            assertEquals(8, position.col)
            assertEquals("23456789", lineText)
        }
    }

    @Test
    fun `can query context from first line that spans multiple buffer`() {
        val stream = BufferingCharStream(bufferLen = 4)

        stream.append("123456789\na".toCharArray())

        stream.contextAt(0).apply {
            assertNotNull(this)
            assertEquals(0, position.offset)
            assertEquals(1, position.line)
            assertEquals(1, position.col)
            assertEquals("123456789", lineText)
        }
        stream.contextAt(2).apply {
            assertNotNull(this)
            assertEquals(2, position.offset)
            assertEquals(1, position.line)
            assertEquals(3, position.col)
            assertEquals("123456789", lineText)
        }
        stream.contextAt(5).apply {
            assertNotNull(this)
            assertEquals(5, position.offset)
            assertEquals(1, position.line)
            assertEquals(6, position.col)
            assertEquals("123456789", lineText)
        }
        stream.contextAt(8).apply {
            assertNotNull(this)
            assertEquals(8, position.offset)
            assertEquals(1, position.line)
            assertEquals(9, position.col)
            assertEquals("123456789", lineText)
        }
    }

    @Test
    fun `can query position at end of line`() {
        val stream = BufferingCharStream()

        stream.append("1\n2\n\n".toCharArray())

        stream.posAt(1).apply {
            assertEquals(1, offset)
            assertEquals(1, line)
            assertEquals(2, col)
        }

        stream.posAt(4).apply {
            assertEquals(4, offset)
            assertEquals(3, line)
            assertEquals(1, col)
        }
    }

    @Test
    fun `can query context at end of line`() {
        val stream = BufferingCharStream()

        stream.append("1\n2\n\n".toCharArray())

        stream.contextAt(1).apply {
            assertNotNull(this)
            assertEquals(1, position.offset)
            assertEquals(1, position.line)
            assertEquals(2, position.col)
            assertEquals("1", lineText)
        }

        stream.contextAt(4).apply {
            assertNotNull(this)
            assertEquals(4, position.offset)
            assertEquals(3, position.line)
            assertEquals(1, position.col)
            assertEquals("", lineText)
        }
    }

    @Test
    fun `can query position at end of input`() {
        val stream = BufferingCharStream()

        stream.append("1\n2\n34".toCharArray())

        stream.posAt(6).apply {
            assertEquals(6, offset)
            assertEquals(3, line)
            assertEquals(3, col)
        }
    }

    @Test
    fun `can query context at end of input`() {
        val stream = BufferingCharStream()

        stream.append("1\n2\n34".toCharArray())

        stream.contextAt(6).apply {
            assertNull(this)
        }

        stream.end()

        stream.contextAt(6).apply {
            assertNotNull(this)
            assertEquals(6, position.offset)
            assertEquals(3, position.line)
            assertEquals(3, position.col)
            assertEquals("34", lineText)
        }
    }
}