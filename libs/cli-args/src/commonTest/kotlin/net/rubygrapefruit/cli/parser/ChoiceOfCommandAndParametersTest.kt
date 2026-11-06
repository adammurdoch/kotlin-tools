package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfCommandAndParametersTest : AbstractParseTest() {
    @Test
    fun `parses one of literal and parameter`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { _, a -> "1:$a" },
            params(command("two")) { "2" },
        )

        assertEquals("1:value", parser.parse("one", "value").get())
        assertEquals("2", parser.parse("two").get())
    }

    @Test
    fun `reports usage`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { _, a -> "1:$a" },
            params(command("two")) { "2" },
        )

        assertEquals(
            """one <a>
two""", parser.usage
        )
    }

    @Test
    fun `literal must be provided`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { _, a -> "1:$a" },
            params(command("two")) { "2" },
        )

        // Capturing current behavior

        parser.fails {
//            failure(0, "Expected 'one' or 'two'")
            failure(0, "Expected 'one'")
            failure(0, "Parameter <a> not provided")
            failure(0, "Expected 'two'")
        }
        parser.fails("unknown") {
//            failure(0, "Unknown argument 'unknown'. Expected 'one' or 'two'")
            failure(0, "Unexpected argument 'unknown'")
            failure(1, "Expected 'one'")
            failure(1, "Parameter <a> not provided")
            failure(1, "Expected 'two'")
        }
    }

    @Test
    fun `parameter must be provided`() {
        val parser = oneOf(
            params(command("one"), positional("a")) { _, a -> "1:$a" },
            params(command("two")) { "2" },
        )

        parser.fails("one") {
            failure(1, "Parameter <a> not provided")
        }
    }
}