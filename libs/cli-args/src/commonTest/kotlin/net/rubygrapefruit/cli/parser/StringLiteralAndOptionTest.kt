package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class StringLiteralAndOptionTest : AbstractParseTest() {
    @Test
    fun `parses literal and option in any order`() {
        val parser = allOf(
            literal("run"),
            option("a").boolean()
        ) { _, a -> a }

        assertEquals(true, parser.parse("run", "-a").get())
        assertEquals(true, parser.parse("-a", "run").get())

        assertEquals(false, parser.parse("run").get())
    }
}