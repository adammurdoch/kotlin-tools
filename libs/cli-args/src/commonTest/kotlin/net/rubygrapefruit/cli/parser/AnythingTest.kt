package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class AnythingTest : AbstractParseTest() {
    @Test
    fun `parses any argument`() {
        val parser = anything()

        parser.edges {
            positional {
                matches("one") {
                    endOfInput("one")
                }
                matches("-x") {
                    endOfInput("-x")
                }
                matches("--long") {
                    endOfInput("--long")
                }
                expecting("an argument")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = anything()

        parser.usage("<argument>")
    }

    @Test
    fun `argument must be provided`() {
        val parser = anything()

        parser.fails {
            failure(0, "Expected an argument")
        }
    }
}