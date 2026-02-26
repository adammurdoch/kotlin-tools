package net.rubygrapefruit.parse.binary

import junit.framework.TestCase.assertEquals
import net.rubygrapefruit.file.file
import net.rubygrapefruit.parse.AbstractFileParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test
import kotlin.test.assertIs

class BinaryFileParseTest : AbstractFileParseTest() {
    @Test
    fun `parses the contents of a file as binary`() {
        val file = fixture.file("binary.bin")
        file.writeBytes(byteArrayOf(0x1, 0x2, 0x3))

        val parser = map(zeroOrMore(oneInRange(0x1, 0x10))) { it.size }

        val result = parser.parse(file.file())
        assertIs<ParseResult.Success<Int>>(result)
        assertEquals(3, result.value)
    }
}