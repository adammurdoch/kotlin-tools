package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class StringLiteralTest : AbstractParseTest() {
    @Test
    fun `parses literal`() {
        val parser = literal("help")

        assertEquals(Unit, parser.parse("help").get())

        parser.fails("HELP") {
            failure(0, "Unexpected argument 'HELP'. Expected 'help' instead")
        }
        parser.fails("--help") {
            failure(0, "Unexpected option '--help'")
            failure(1, "Expected 'help'")
        }
    }

    @Test
    fun `reports usage`() {
        val parser = literal("help")

        assertEquals("help", parser.usage)
    }

    @Test
    fun `literal must be provided`() {
        val parser = literal("help")

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
        val parser = literal("help")

        parser.fails("help", "help") {
            failure(1, "Unexpected argument 'help'")
        }
        parser.fails("help", "help", "help") {
            failure(1, "Unexpected argument 'help'")
            failure(2, "Unexpected argument 'help'")
        }
    }
}