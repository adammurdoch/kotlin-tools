package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class NoParametersTest : AbstractParseTest() {
    @Test
    fun `parses no parameters`() {
        val parser = params { "result" }

        assertEquals("result", parser.parse().get())
    }

    @Test
    fun `cannot include any input values`() {
        val parser = params { fail() }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'")
        }
    }
}