package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class OneOrMoreAnythingTest : AbstractParseTest() {
    @Test
    fun `parses one or more arguments`() {
        val parser = anything().oneOrMore()

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
                expecting("an argument")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = anything().oneOrMore()

        parser.usage("<argument>...")
        parser.noOptions()
        parser.noPositionals()
    }

    @Test
    fun `argument must be provided`() {
        val parser = anything().oneOrMore()

        parser.fails {
            failure(0, "Expected an argument")
        }
    }
}