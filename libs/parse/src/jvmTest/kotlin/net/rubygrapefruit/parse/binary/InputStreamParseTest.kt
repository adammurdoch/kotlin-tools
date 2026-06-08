package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import java.io.ByteArrayInputStream
import kotlin.test.Test

class InputStreamParseTest : AbstractParseTest() {
    @Test
    fun `parses the contents of an input stream`() {
        val parser = map(zeroOrMore(oneInRange(0x1, 0x10))) { it.size }

        val result = parser.parseStreamContaining(byteArrayOf(0x1, 0x2, 0x3))
        result.assertIsSuccess(3)
    }

    @Test
    fun `fails when stream contains additional bytes`() {
        val parser = oneInRange(0x1, 0x10)

        val result = parser.parseStreamContaining(byteArrayOf(0x1, 0x2))
        result.assertIsFail {
            failAt(1)
            expectEndOfInput()
            expectContext("x02")
        }
    }

    private fun <OUT> Parser<BinaryInput, OUT>.parseStreamContaining(bytes: ByteArray): ParseResult<BinaryFailureContext, OUT> {
        val inputStream = ByteArrayInputStream(bytes)

        return parse(inputStream)
    }
}