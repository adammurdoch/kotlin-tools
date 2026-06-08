package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import java.io.StringReader
import kotlin.test.Test

class ReaderParseTest : AbstractParseTest() {
    @Test
    fun `parses the contents of a reader`() {
        val parser = map(zeroOrMore(oneInRange('a'..'z'))) { it.size }

        val result = parser.parseStreamContaining("abc")
        result.assertIsSuccess(3)
    }

    @Test
    fun `fails when file contains additional text`() {
        val parser = oneInRange('a'..'z')

        val result = parser.parseStreamContaining("abc")
        result.assertIsFail {
            failAt(1)
            expectEndOfInput()
            expectContext("a", "bc")
        }
    }

    private fun <OUT> Parser<TextInput, OUT>.parseStreamContaining(text: String): ParseResult<TextFailureContext, OUT> {
        return parse(StringReader(text))
    }
}