package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class OneOrMoreAnythingTest : AbstractParseTest() {
    @Test
    fun `parses one or more arguments`() {
        val parser = anything("a").oneOrMore()

        parser.edges {
            positional {
                matches("value 1") {
                    positional {
                        matches("value 2") {
                            positional()
                            endOfInput(listOf("value 1", "value 2"))
                        }
                        matches("") {
                            positional {
                                matches("value 2") {
                                    positional()
                                    endOfInput(listOf("value 1", "", "value 2"))
                                }
                            }
                            endOfInput(listOf("value 1", ""))
                        }
                    }
                    endOfInput(listOf("value 1"))
                }
                expectingPositional("a")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = anything("a").oneOrMore()

        parser.usage("<a>...")
        parser.noOptions()
        parser.positional("<a>")
    }

    @Test
    fun `argument must be provided`() {
        val parser = anything("a").oneOrMore()

        parser.fails {
            expectedPositional(0, "a")
        }
    }
}