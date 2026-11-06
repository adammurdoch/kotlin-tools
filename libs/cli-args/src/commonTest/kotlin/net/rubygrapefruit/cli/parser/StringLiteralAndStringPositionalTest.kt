package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class StringLiteralAndStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses literal then parameter`() {
        val parser = params(literal("run"), positional("a")) { _, a -> a }

        assertEquals("one", parser.parse("run", "one").get())
        assertEquals("run", parser.parse("run", "run").get())
    }

    @Test
    fun `parses parameter then literal`() {
        val parser = params(positional("a"), literal("run")) { a, _ -> a }

        assertEquals("one", parser.parse("one", "run").get())
        assertEquals("run", parser.parse("run", "run").get())
    }

    @Test
    fun `literal must be provided`() {
        val parser = params(literal("run"), positional("a")) { _, a -> a }

        parser.fails {
            failure(0, "Expected 'run'")
            failure(0, "Parameter <a> not provided")
        }
        parser.fails("one") {
            failure(0, "Unexpected argument 'one'. Expected 'run' instead")
            failure(1, "Parameter <a> not provided")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Expected 'run'")
            failure(1, "Parameter <a> not provided")
        }
    }

    @Test
    fun `parameter must be provided`() {
        val parser = params(literal("run"), positional("a")) { _, a -> a }

        parser.fails("run") {
            failure(1, "Parameter <a> not provided")
        }
        parser.fails("run", "--unknown") {
            failure(1, "Unexpected option '--unknown'")
            failure(2, "Parameter <a> not provided")
        }
    }
}