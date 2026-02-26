package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.combinators.oneOrMore
import kotlin.test.Test

class TextPushParserTest : AbstractParseTest() {
    @Test
    fun `can reuse input buffer for each input chunk`() {
        val parser = oneOrMore(oneInRange('a'..'z'), separator = literal(","))

        val pushParser = parser.pushParser()
        val buffer = CharArray(10)

        buffer[5] = 'a'
        buffer[6] = ','
        buffer[7] = 'b'

        pushParser.input(buffer, 5, 3)

        buffer[0] = ','
        buffer[1] = 'c'

        pushParser.input(buffer, 0, 2)

        val result = pushParser.endOfInput()
        result.assertIsSuccess(listOf('a', 'b', 'c'))
    }
}