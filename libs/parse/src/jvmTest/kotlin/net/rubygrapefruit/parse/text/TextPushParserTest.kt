package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.general.succeed
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class TextPushParserTest : AbstractParseTest() {
    @Test
    fun `can parse empty stream`() {
        val parser = succeed(1)

        val pushParser = parser.pushParser()

        val result = pushParser.endOfInput()
        result.assertIsSuccess(1)
    }

    @Test
    fun `returns failure on end of input when empty stream provided`() {
        val parser = literal("abc", 1)

        val pushParser = parser.pushParser()

        val result = pushParser.endOfInput()
        assertIs<ParseResult.Fail<*>>(result)
    }

    @Test
    fun `returns failure when parse fails`() {
        val parser = zeroOrMore(literal("a", 1), separator = literal(","))

        val pushParser = parser.pushParser()

        val result = pushParser.input("a,X".toCharArray())
        assertIs<ParseResult.Fail<*>>(result)
    }

    @Test
    fun `returns failure on end of input when parse fails`() {
        val parser = zeroOrMore(literal("a", 1), separator = literal(","))

        val pushParser = parser.pushParser()

        val result = pushParser.input("a,".toCharArray())
        assertNull(result)

        val result2 = pushParser.endOfInput()
        assertIs<ParseResult.Fail<*>>(result2)
    }

    @Test
    fun `can reuse input buffer for each input chunk`() {
        val parser = oneOrMore(oneInRange('a'..'z'), separator = literal(","))

        val pushParser = parser.pushParser()
        val buffer = CharArray(10)

        buffer[5] = 'a'
        buffer[6] = ','
        buffer[7] = 'b'

        val r1 = pushParser.input(buffer, 5, 3)
        assertNull(r1)

        buffer[0] = ','
        buffer[1] = 'c'

        val r2 = pushParser.input(buffer, 0, 2)
        assertNull(r2)

        val result = pushParser.endOfInput()
        result.assertIsSuccess(listOf('a', 'b', 'c'))
    }
}