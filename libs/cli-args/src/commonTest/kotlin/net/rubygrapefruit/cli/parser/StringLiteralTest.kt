package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class StringLiteralTest : AbstractParseTest() {
    @Test
    fun `parses literal`() {
        val parser = command("help")

        assertEquals(Unit, parser.parse("help").get())
    }

    @Test
    fun `reports usage`() {
        val parser = command("help")

        assertEquals("help", parser.usage)
    }

    @Test
    fun `cannot include other input values`() {
        val parser = command("help")

       parser.fails("HELP") {
            failure(0, "Unexpected argument 'HELP'. Expected 'help' instead")
        }
        parser.fails("help", "HELP") {
            failure(1, "Unexpected argument 'HELP'")
        }
        parser.fails("HELP", "help") {
            failure(0, "Unexpected argument 'HELP'")
        }
    }

    @Test
    fun `literal must be provided`() {
        val parser = command("help")

        parser.fails {
            failure(0, "Expected 'help'")
        }
        parser.fails("other") {
            failure(0, "Unexpected argument 'other'. Expected 'help' instead")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
            failure(1, "Expected 'help'")
        }
    }

    @Test
    fun `literal cannot appear more than once`() {
        val parser = command("help")

        parser.fails("help", "help") {
            failure(1, "Unexpected argument 'help'")
        }
        parser.fails("help", "help", "help") {
            failure(1, "Unexpected argument 'help'")
            failure(2, "Unexpected argument 'help'")
        }
    }
}