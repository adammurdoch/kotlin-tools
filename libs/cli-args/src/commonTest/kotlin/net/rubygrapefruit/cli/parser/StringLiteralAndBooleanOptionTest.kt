package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringLiteralAndBooleanOptionTest : AbstractParseTest() {
    @Test
    fun `parses literal and option in any order`() {
        val parser = params(
            command("run"),
            option("a").boolean()
        ) { _, a -> a }

        assertEquals(true, parser.parse("run", "-a").get())
        assertEquals(true, parser.parse("-a", "run").get())

        assertEquals(false, parser.parse("run").get())
    }

    @Test
    fun `must provide literal`() {
        val parser = params(
            command("run"),
            option("a").boolean()
        ) { _, _ -> fail() }

        parser.fails {
            failure(0, "Expected 'run'")
        }
        parser.fails("-a") {
            failure(1, "Expected 'run'")
        }
        parser.fails("-a", "unknown") {
            failure(1, "Unexpected argument 'unknown'. Expected 'run' instead")
        }
        parser.fails("unknown", "-a") {
            failure(0, "Unexpected argument 'unknown'. Expected 'run' instead")
        }
        parser.fails("-a", "-unknown") {
            failure(1, "Unexpected option '-unknown'")
            failure(2, "Expected 'run'")
        }
    }

    @Test
    fun `option must not have a value`() {
        val parser = params(
            command("run"),
            option("a").boolean()
        ) { _, _ -> fail() }

        parser.fails("run", "-a", "value") {
            failure(2, "Unexpected argument 'value'")
        }
        parser.fails("-a", "value", "run") {
            failure(1, "Unexpected argument 'value'")
        }
    }
}