package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class RequiredStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x").required()

        assertEquals("value", parser.parse("-x", "value").get())
    }

    @Test
    fun `reports usage of option with short name`() {
        val parser = option("x").required()

        parser.usage("-x <value>")
        parser.option("-x <value>")
    }

    @Test
    fun `can provide summary`() {
        val parser = option("x", summary = "Something useful").required()

        parser.usage("-x <value>")
        parser.option("-x <value>", "Something useful")
    }

    @Test
    fun `option must be present`() {
        val parser = option("some-flag", "x").required()

        parser.fails {
            failure(0, "Option --some-flag not provided")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Option --some-flag not provided")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'")
            failure(1, "Option --some-flag not provided")
        }
    }
}