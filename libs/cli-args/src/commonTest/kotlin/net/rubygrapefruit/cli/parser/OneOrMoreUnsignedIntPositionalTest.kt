package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class OneOrMoreUnsignedIntPositionalTest : AbstractParseTest() {
    @Test
    fun `parses one or more positional parameters`() {
        val parser = positional("a", unsignedInteger()).oneOrMore()

        parser.edges {
            positional {
                matches("101") {
                    positional {
                        matches("102") {
                            positional()
                            endOfInput(listOf(101, 102))
                        }
                    }
                    endOfInput(listOf(101))
                }
                expectingPositional("a")
            }
        }
    }

    @Test
    fun `positional must be provided`() {
        val parser = positional("a", unsignedInteger()).oneOrMore()

        parser.fails {
            expectedPositional(0, "a")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'. Expected parameter <a> instead")
        }
    }

    @Test
    fun `positional must be an int`() {
        val parser = positional("a", unsignedInteger()).oneOrMore()

        parser.fails("one") {
            failure(0, "Unexpected argument 'one'. Expected an unsigned int instead")
        }
        parser.fails("1", "two") {
            failure(1, "Unexpected argument 'two'. Expected an unsigned int instead")
        }
        parser.fails("1", "two", "3") {
            failure(1, "Unexpected argument 'two'. Expected an unsigned int instead")
        }
        parser.fails("one", "2", "three") {
            failure(0, "Unexpected argument 'one'. Expected an unsigned int instead")
            failure(2, "Unexpected argument 'three'. Expected an unsigned int instead")
        }
    }
}