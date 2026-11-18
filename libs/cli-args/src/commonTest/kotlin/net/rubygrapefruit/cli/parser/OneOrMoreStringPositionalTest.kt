package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class OneOrMoreStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses one or more positional parameters`() {
        val parser = positional("a").oneOrMore()

        parser.edges {
            positional {
                matches("value 1") {
                    positional {
                        matches("value 2") {
                            positional { }
                            endOfInput(listOf("value 1", "value 2"))
                        }
                        matches("") {
                            positional {
                                matches("value 2") {
                                    positional { }
                                    endOfInput(listOf("value 1", "", "value 2"))
                                }
                            }
                            endOfInput(listOf("value 1", ""))
                        }
                    }
                    endOfInput(listOf("value 1"))
                }
            }
            aborts("Parameter <a> not provided")
        }
    }

    @Test
    fun `reports usage`() {
        val parser = positional("a").oneOrMore()

        parser.usage("<a>...")
        parser.noOptions()
        parser.positional("<a>")
    }

    @Test
    fun `can provide summary`() {
        val parser = positional("a", summary = "Something useful").oneOrMore()
        parser.usage("<a>...")
        parser.positional("<a>", "Something useful")
    }

    @Test
    fun `positional must be provided`() {
        val parser = positional("a").oneOrMore()

        parser.fails {
            failure(0, "Parameter <a> not provided")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
            failure(1, "Parameter <a> not provided")
        }
    }

    @Test
    fun `cannot include other input`() {
        val parser = positional("a").oneOrMore()

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