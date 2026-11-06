package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class StringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses string`() {
        val parser = positional("param")

        assertEquals("value", parser.parse("value").get())
        assertEquals("", parser.parse("").get())
    }

    @Test
    fun `reports usage`() {
        val parser = positional("param")

        assertEquals("<param>", parser.usage)
    }

    @Test
    fun `parameter must be provided`() {
        val parser = positional("param")

        parser.fails {
            failure(0, "Parameter <param> not provided")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
            failure(1, "Parameter <param> not provided")
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = positional("param")

        parser.fails("value", "other") {
            failure(1, "Unexpected argument 'other'")
        }
        parser.fails("-unknown", "value") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("value", "-unknown") {
            failure(1, "Unexpected option '-unknown'")
        }
    }
}