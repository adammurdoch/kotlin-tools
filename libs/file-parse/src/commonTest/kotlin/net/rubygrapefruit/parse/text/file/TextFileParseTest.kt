package net.rubygrapefruit.parse.text.file

import net.rubygrapefruit.file.fixtures.AbstractFileTest
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.one
import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TextFileParseTest : AbstractFileTest() {
    @Test
    fun `parses file`() {
        val parser = map(oneOrMore(oneInRange('a'..'z'))) { it.size }

        val file = fixture.file("text")
        file.writeText("abc")

        val result = parser.parse(file)

        assertIs<ParseResult.Success<*>>(result)
        assertEquals(3, result.get())
    }

    @Test
    fun `parses file containing non-ascii chars`() {
        val parser = match(oneOrMore(one()))

        val file = fixture.file("text")

        val candidates = listOf(
            "abc",
            "βπΨ",
            "食べる",
            "\u0080\u07FF\u0800\uF000\uFFFF"
        )

        for (candidate in candidates) {
            file.writeText(candidate)

            val result = parser.parse(file)

            assertIs<ParseResult.Success<*>>(result)
            assertEquals(candidate, result.get())
        }
    }
}