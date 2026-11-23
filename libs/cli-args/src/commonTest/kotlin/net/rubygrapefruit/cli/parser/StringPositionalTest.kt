package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class StringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses positional`() {
        val parser = positional("param")

        parser.edges {
            positional {
                matches("value") {
                    endOfInput("value")
                }
                matches("") {
                    endOfInput("")
                }
                doesNotMatch("-x")
                expectingPositional("param")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = positional("param")

        parser.usage("<param>")
        parser.noOptions()
        parser.positional("<param>")
    }

    @Test
    fun `reports usage when summary provided`() {
        val parser = positional("param", summary = "Something useful")

        parser.usage("<param>")
        parser.noOptions()
        parser.positional("<param>", "Something useful")
    }

    @Test
    fun `positional must be provided`() {
        val parser = positional("param")

        parser.fails {
            expectedPositional(0, "param")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'. Expected parameter <param> instead")
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