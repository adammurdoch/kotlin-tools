package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ZeroOrMoreAnythingTest : AbstractParseTest() {
    @Test
    fun `parses zero or more arguments`() {
        val parser = anything().zeroOrMore()

        parser.edges {
            positional {
                matches("one") {
                    positional {
                        matches("two") {
                            positional()
                            endOfInput(listOf("one", "two"))
                        }
                        matches("--two") {
                            positional()
                            endOfInput(listOf("one", "--two"))
                        }
                    }
                    endOfInput(listOf("one"))
                }
                matches("--one") {
                    positional()
                    endOfInput(listOf("--one"))
                }
            }
            endOfInput(emptyList<String>())
        }
    }

    @Test
    fun `reports usage`() {
        val parser = anything().zeroOrMore()

        parser.usage("[<argument>...]")
        parser.noOptions()
        parser.noPositionals()
    }
}