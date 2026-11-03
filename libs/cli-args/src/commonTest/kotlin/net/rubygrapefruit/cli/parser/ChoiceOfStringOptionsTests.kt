package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfStringOptionsTests : AbstractParseTest() {
    @Test
    fun `parses one of string options`() {
        val parser = oneOf(
            option("a"),
            option("b"),
            option("c")
        )

        assertEquals("one", parser.parse("-a", "one").get())
        assertEquals("two", parser.parse("-b", "two").get())
        assertEquals("three", parser.parse("-c", "three").get())

        assertEquals(null, parser.parse().get())
    }
}