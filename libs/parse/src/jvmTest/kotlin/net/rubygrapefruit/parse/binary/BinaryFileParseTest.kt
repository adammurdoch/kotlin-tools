package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.file.file
import net.rubygrapefruit.parse.AbstractFileParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class BinaryFileParseTest : AbstractFileParseTest() {
    @Test
    fun `parses the contents of a file`() {
        val parser = map(zeroOrMore(oneInRange(0x1, 0x10))) { it.size }

        val result = parser.parseFileContaining(byteArrayOf(0x1, 0x2, 0x3))
        result.assertIsSuccess(3)
    }

    @Test
    fun `fails when file contains additional bytes`() {
        val parser = oneInRange(0x1, 0x10)

        val result = parser.parseFileContaining(byteArrayOf(0x1, 0x2))
        result.assertIsFail {
            failAt(1)
            expectEndOfInput()
            expectContext("x02")
        }
    }

    @Test
    fun `reports parse failure`() {
        val parser = map(zeroOrMore(oneInRange(0x1, 0x10))) { it.size }

        val result = parser.parseFileContaining(byteArrayOf(0x1, 0x2, 0, 0x3))
        result.assertIsFail {
            failAt(2)
            expectOneInRange(0x1, 0x10)
            expectEndOfInput()
            expectContext("x0")
        }
    }

    private fun <OUT> Parser<BinaryInput, OUT>.parseFileContaining(bytes: ByteArray): ParseResult<BinaryFailureContext, OUT> {
        val file = fixture.file("binary.bin")
        file.writeBytes(bytes)

        return parse(file.file())
    }
}