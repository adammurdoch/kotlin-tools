package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.combinators.oneOrMore
import kotlin.test.Test

class BinaryPushParserTest : AbstractParseTest() {
    @Test
    fun `can reuse input buffer for each input chunk`() {
        val parser = oneOrMore(oneInRange(0x1, 0x3), separator = literal(byteArrayOf(0)))

        val pushParser = parser.pushParser()
        val buffer = ByteArray(10)

        buffer[5] = 0x3
        buffer[6] = 0
        buffer[7] = 0x1

        pushParser.input(buffer, 5, 3)

        buffer[0] = 0
        buffer[1] = 0x2

        pushParser.input(buffer, 0, 2)

        val result = pushParser.endOfInput()
        result.assertIsSuccess(bytes(0x3, 0x1, 0x2))
    }
}