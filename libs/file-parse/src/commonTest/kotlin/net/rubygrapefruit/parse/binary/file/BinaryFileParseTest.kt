package net.rubygrapefruit.parse.binary.file

import net.rubygrapefruit.file.fixtures.AbstractFileTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.binary.oneInRange
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.oneOrMore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BinaryFileParseTest : AbstractFileTest() {
    @Test
    fun `parses file`() {
        val parser = map(oneOrMore(oneInRange(0x1, 0x9))) { it.size }

        val file = fixture.file("text")
        file.writeBytes(byteArrayOf(0x2, 0x3, 0x6))

        val result = parser.parse(file)

        assertIs<ParseResult.Success<*>>(result)
        assertEquals(3, result.get())
    }
}