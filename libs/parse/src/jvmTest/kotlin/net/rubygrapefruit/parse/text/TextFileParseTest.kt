package net.rubygrapefruit.parse.text

import net.rubygrapefruit.file.file
import net.rubygrapefruit.parse.AbstractFileParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TextFileParseTest : AbstractFileParseTest() {
    @Test
    fun `parses the contents of a file as text`() {
        val file = fixture.file("text.txt")
        file.writeText("abc")

        val parser = map(zeroOrMore(oneInRange('a'..'z'))) { it.size }

        val result = parser.parse(file.file(), Charsets.UTF_8)
        assertIs<ParseResult.Success<Int>>(result)
        assertEquals(3, result.value)
    }
}