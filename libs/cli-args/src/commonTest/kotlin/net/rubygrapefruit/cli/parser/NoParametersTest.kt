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
    fun `reports usage`() {
        val parser = params { fail() }

        parser.usage("")
        parser.noOptions()
    }

    @Test
    fun `can provide summary`() {
        val parser = params("Something useful") { fail() }

        parser.usage("", "Something useful")
        parser.noOptions()
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
        parser.fails("") {
            failure(0, "Unexpected argument ''")
        }
    }
}