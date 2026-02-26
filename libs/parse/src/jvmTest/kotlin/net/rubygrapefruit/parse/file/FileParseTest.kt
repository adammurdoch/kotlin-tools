package net.rubygrapefruit.parse.file

import net.rubygrapefruit.file.file
import net.rubygrapefruit.file.fixtures.FilesFixture
import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FileParseTest : AbstractParseTest() {
    val fixture = FilesFixture()

    @AfterTest
    fun cleanup() {
        fixture.cleanup()
    }

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