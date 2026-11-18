package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ZeroOrMoreStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses zero or more positional parameters`() {
        val parser = positional("a").zeroOrMore()

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
            }
            endOfInput(emptyList<String>())
        }
    }

    @Test
    fun `reports usage`() {
        val parser = positional("a").zeroOrMore()

        parser.usage("[<a>...]")
        parser.noOptions()
        parser.positional("<a>")
    }

    @Test
    fun `can provide summary`() {
        val parser = positional("a", summary = "Something useful").zeroOrMore()
        parser.usage("[<a>...]")
        parser.positional("<a>", "Something useful")
    }

    @Test
    fun `cannot include other input`() {
        val parser = positional("a").zeroOrMore()

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
        }
        parser.fails("--unknown", "one") {
            failure(0, "Unexpected option '--unknown'")
        }
        parser.fails("one", "--unknown", "two") {
            failure(1, "Unexpected option '--unknown'")
        }
        parser.fails("one", "-u", "two", "--unknown") {
            failure(1, "Unexpected option '-u'")
            failure(3, "Unexpected option '--unknown'")
        }
    }
}