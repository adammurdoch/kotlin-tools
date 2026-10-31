package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class StringOptionTest : AbstractParseTest() {
    @Test
    fun `parses empty args`() {
        val parser = option("x")

        assertEquals("value", parser.parse(listOf("-x", "value")).get())
        assertNull(parser.parse(listOf()).get())

        assertIs<Parser.Failure>(parser.parse(listOf("value")))
        assertIs<Parser.Failure>(parser.parse(listOf("-x")))
        assertIs<Parser.Failure>(parser.parse(listOf("-X", "value")))
        assertIs<Parser.Failure>(parser.parse(listOf("--x", "value")))
        assertIs<Parser.Failure>(parser.parse(listOf("-x", "-y")))
        assertIs<Parser.Failure>(parser.parse(listOf("-unknown", "value")))
    }
}