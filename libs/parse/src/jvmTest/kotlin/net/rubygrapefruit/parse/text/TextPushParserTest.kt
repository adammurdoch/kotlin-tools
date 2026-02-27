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
    fun `can signal end of input without supplying any input`() {
        val parser = succeed(1)

        val pushParser = parser.pushParser()

        val result = pushParser.endOfInput()
        result.assertIsSuccess(1)
    }

    @Test
    fun `returns failure on end of input when no input supplied`() {
        val parser = literal("abc", 1)

        val pushParser = parser.pushParser()

        val result = pushParser.endOfInput()
        assertIs<ParseResult.Fail<*>>(result)
    }

    @Test
    fun `returns failure when parse fails and end of line has been supplied`() {
        val parser = zeroOrMore(literal("a", 1), separator = literal(","))

        val pushParser = parser.pushParser()

        val result = pushParser.input("a,X\n".toCharArray())
        assertIs<ParseResult.Fail<*>>(result)
    }

    @Test
    fun `continues when parse fails and end of line has not been supplied`() {
        val parser = zeroOrMore(literal("a", 1), separator = literal(","))

        val pushParser = parser.pushParser()

        val result1 = pushParser.input("a,X".toCharArray())
        assertNull(result1)

        val result2 = pushParser.input("X\n".toCharArray())
        assertIs<ParseResult.Fail<*>>(result2)
    }

    @Test
    fun `returns failure on end of input when parse fails`() {
        val parser = zeroOrMore(literal("a", 1), separator = literal(","))

        val pushParser = parser.pushParser()

        val result1 = pushParser.input("a,".toCharArray())
        assertNull(result1)

        val result2 = pushParser.endOfInput()
        assertIs<ParseResult.Fail<*>>(result2)
    }

    @Test
    fun `can continue to supply input after parse failure`() {
        val parser = zeroOrMore(literal("a", 1), separator = literal(","))

        val pushParser = parser.pushParser()

        val result1 = pushParser.input("a,X\n".toCharArray())
        assertIs<ParseResult.Fail<*>>(result1)

        val result2 = pushParser.input("XX".toCharArray())
        assertIs<ParseResult.Fail<*>>(result2)

        // empty
        val result3 = pushParser.input(CharArray(0))
        assertIs<ParseResult.Fail<*>>(result3)

        val result4 = pushParser.endOfInput()
        assertIs<ParseResult.Fail<*>>(result4)
    }

    @Test
    fun `can reuse input buffer for each input chunk`() {
        val parser = oneOrMore(oneInRange('a'..'z'), separator = literal(","))

        val pushParser = parser.pushParser()
        val buffer = CharArray(10)

        buffer[5] = 'a'
        buffer[6] = ','
        buffer[7] = 'b'

        val result1 = pushParser.input(buffer, 5, 3)
        assertNull(result1)

        buffer[0] = ','
        buffer[1] = 'c'

        val result2 = pushParser.input(buffer, 0, 2)
        assertNull(result2)

        val result3 = pushParser.endOfInput()
        result3.assertIsSuccess(listOf('a', 'b', 'c'))
    }
}