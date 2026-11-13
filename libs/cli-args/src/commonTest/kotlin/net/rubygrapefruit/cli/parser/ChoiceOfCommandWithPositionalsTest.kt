package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfCommandWithPositionalsTest : AbstractParseTest() {
    @Test
    fun `parses one of literal and positional`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { a -> "1:$a" },
            params(command("two")) { "2" },
        )

        assertEquals("1:value", parser.parse("one", "value").get())
        assertEquals("2", parser.parse("two").get())
    }

    @Test
    fun `reports usage`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { a -> "1:$a" },
            params(command("two")) { "2" },
        )

        parser.usage {
            choice("one <a>")
            choice("two")
        }
        parser.noOptions()
    }

    @Test
    fun `literal must be provided`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { a -> "1:$a" },
            params(command("two")) { "2" },
        )

        parser.fails {
            failure(0, "Expected 'one' or 'two'")
        }
        parser.fails("unknown") {
            failure(0, "Unknown argument 'unknown'. Expected 'one' or 'two' instead")
        }
    }

    @Test
    fun `positional must be provided`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { a -> "1:$a" },
            params(command("two")) { "2" },
        )

        parser.fails("one") {
            failure(1, "Parameter <a> not provided")
        }
    }
}