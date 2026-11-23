package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class AnythingTest : AbstractParseTest() {
    @Test
    fun `parses any argument`() {
        val parser = anything("param")

        parser.edges {
            positional {
                matches("one") {
                    endOfInput("one")
                }
                matches("") {
                    endOfInput("")
                }
                matches("-x") {
                    endOfInput("-x")
                }
                matches("--long") {
                    endOfInput("--long")
                }
                expectingPositional("param")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = anything("param")

        parser.usage("<param>")
        parser.noOptions()
        parser.positional("<param>")
    }

    @Test
    fun `can provide summaru`() {
        val parser = anything("param", summary = "something useful")

        parser.usage("<param>")
        parser.noOptions()
        parser.positional("<param>", "something useful")
    }

    @Test
    fun `argument must be provided`() {
        val parser = anything("param")

        parser.fails {
            expectedPositional(0, "param")
        }
    }
}