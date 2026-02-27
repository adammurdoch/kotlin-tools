package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.file.file
import net.rubygrapefruit.parse.AbstractFileParseTest
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class BinaryFileParseTest : AbstractFileParseTest() {
    @Test
    fun `parses the contents of a file as binary`() {
        val file = fixture.file("binary.bin")
        file.writeBytes(byteArrayOf(0x1, 0x2, 0x3))

        val parser = map(zeroOrMore(oneInRange(0x1, 0x10))) { it.size }

        val result = parser.parse(file.file())
        result.assertIsSuccess(3)
    }

    @Test
    fun `reports parse failure`() {
        val file = fixture.file("binary.bin")
        file.writeBytes(byteArrayOf(0x1, 0x2, 0, 0x3))

        val parser = map(zeroOrMore(oneInRange(0x1, 0x10))) { it.size }

        val result = parser.parse(file.file())
        result.assertIsFail {
            failAt(2)
            expectOneInRange(0x1, 0x10)
            expectEndOfInput()
        }
    }
}