package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.general.succeed
import net.rubygrapefruit.parse.text.pushParser
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class BinaryPushParserTest : AbstractParseTest() {
    @Test
    fun `can signal end of input without supplying any input`() {
        val parser = succeed(1)

        val pushParser = parser.pushParser()

        val result = pushParser.endOfInput()
        result.assertIsSuccess(1)
    }

    @Test
    fun `returns failure on end of input when no input supplied`() {
        val parser = literal(byteArrayOf(0x1, 0x2), 1)

        val pushParser = parser.pushParser()

        val result = pushParser.endOfInput()
        result.assertIsFail {
            expectLiteral(0x1)
        }
    }

    @Test
    fun `returns failure when parse fails and end of line has been supplied`() {
        val parser = zeroOrMore(literal(byteArrayOf(0x1), 1), separator = literal(byteArrayOf(0x2)))

        val pushParser = parser.pushParser()

        val result = pushParser.input(byteArrayOf(0x1, 0x2, 0x3, 0, 0))
        result.assertIsFail {
            failAt(2)
            expectLiteral(0x1)
        }
    }

    @Test
    fun `returns failure on end of input when parse fails`() {
        val parser = zeroOrMore(literal(byteArrayOf(0x1), 1), separator = literal(byteArrayOf(0x2)))

        val pushParser = parser.pushParser()

        val result1 = pushParser.input(byteArrayOf(0x1, 0x2))
        assertNull(result1)

        val result2 = pushParser.endOfInput()
        result2.assertIsFail {
            failAt(2)
            expectLiteral(0x1)
        }
    }

    @Test
    fun `can continue to supply input after parse failure`() {
        val parser = zeroOrMore(literal(byteArrayOf(0x1), 1), separator = literal(byteArrayOf(0x2)))

        val pushParser = parser.pushParser()

        val result1 = pushParser.input(byteArrayOf(0x1, 0x2, 0x3))
        assertNotNull(result1)
        result1.assertIsFail {
            failAt(2)
            expectLiteral(0x1)
        }

        val result2 = pushParser.input(byteArrayOf(0, 0))
        assertSame(result1, result2)

        // empty
        val result3 = pushParser.input(byteArrayOf())
        assertSame(result1, result3)

        val result4 = pushParser.endOfInput()
        assertSame(result1, result4)
    }

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