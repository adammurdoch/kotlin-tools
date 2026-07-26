package net.rubygrapefruit.parse.text

import net.rubygrapefruit.file.file
import net.rubygrapefruit.parse.AbstractFileParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class TextFileParseTest : AbstractFileParseTest() {
    @Test
    fun `parses the contents of a file`() {
        val parser = map(zeroOrMore(oneInRange('a'..'z'))) { it.size }

        val result = parser.parseFileContaining("abc")
        result.assertIsSuccess(3)
    }

    @Test
    fun `fails when file contains additional text`() {
        val parser = oneInRange('a'..'z')

        val result = parser.parseFileContaining("abc")
        result.assertIsFail {
            failAt(1)
            expectEndOfInput()
            expectContext("a", "bc")
        }
    }

    @Test
    fun `reports parse failure`() {
        val parser = map(zeroOrMore(oneOf('a', 'b', '\n'))) { it.size }

        val result = parser.parseFileContaining("ab\naXb")
        result.assertIsFail {
            failAt(4, line = 2, col = 2)
            expectLiteral("a")
            expectLiteral("b")
            expectNewLine()
            expectEndOfInput()
            expectContext("a", "Xb")
        }
    }

    private fun <OUT> Parser<TextInput, OUT>.parseFileContaining(text: String): ParseResult<TextFailureContext, OUT> {
        val file = fixture.file("binary.bin")
        file.writeText(text)

        return parse(file.file(), Charsets.UTF_8)
    }
}