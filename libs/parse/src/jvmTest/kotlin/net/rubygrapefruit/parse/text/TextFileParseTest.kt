package net.rubygrapefruit.parse.text

import net.rubygrapefruit.file.file
import net.rubygrapefruit.parse.AbstractFileParseTest
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class TextFileParseTest : AbstractFileParseTest() {
    @Test
    fun `parses the contents of a file as text`() {
        val file = fixture.file("text.txt")
        file.writeText("abc")

        val parser = map(zeroOrMore(oneInRange('a'..'z'))) { it.size }

        val result = parser.parse(file.file(), Charsets.UTF_8)
        result.assertIsSuccess(3)
    }
}