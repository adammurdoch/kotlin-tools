package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfCommandTest : AbstractParseTest() {
    @Test
    fun `parses one of command`() {
        val parser = oneOf(
            params(command("one")) { 1 },
            params(command("two")) { 2 },
        )

        assertEquals(1, parser.parse("one").get())
        assertEquals(2, parser.parse("two").get())
    }

    @Test
    fun `reports usage`() {
        val parser = oneOf(
            params(command("one")) { 1 },
            params(command("two")) { 2 },
        )

        parser.usage {
            choice("one")
            choice("two")
        }
        parser.noOptions()
        parser.noPositionals()
    }

    @Test
    fun `command must be provided`() {
        val parser = oneOf(
            params(command("one")) { 1 },
            params(command("two")) { 2 },
        )

        parser.fails {
            failure(0, "Expected 'one' or 'two'")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'. Expected 'one' or 'two' instead")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
            failure(1, "Expected 'one' or 'two'")
        }
    }
}