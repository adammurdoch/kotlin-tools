package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfStringLiteralTest : AbstractParseTest() {
    @Test
    fun `parses one of literal`() {
        val parser = oneOf(
            allOf(literal("one")) { 1 },
            allOf(literal("two")) { 2 },
        )

        assertEquals(1, parser.parse("one").get())
        assertEquals(2, parser.parse("two").get())
    }

    @Test
    fun `reports usage`() {
        val parser = oneOf(
            allOf(literal("one")) { 1 },
            allOf(literal("two")) { 2 },
        )

        assertEquals(
            """one
two""", parser.usage
        )
    }

    @Test
    fun `literal must be provided`() {
        val parser = oneOf(
            allOf(literal("one")) { 1 },
            allOf(literal("two")) { 2 },
        )

        parser.fails {
            failure(0, "Expected 'one' or 'two'")
        }
        parser.fails("unknown") {
            failure(0, "Unknown argument 'unknown'. Expected 'one' or 'two'")
        }
    }
}