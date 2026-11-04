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

    @Test
    fun `must provide literal`() {
        val parser = allOf(
            literal("run"),
            option("a").boolean()
        ) { _, a -> a }

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
}