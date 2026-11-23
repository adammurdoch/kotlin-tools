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
            expectedParameter(0, "a")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'. Expected parameter <a> instead")
        }
    }

    @Test
    fun `positional must be an int`() {
        val parser = positional("a", unsignedInteger()).oneOrMore()

        parser.fails("one") {
            failure(0, "'one' is not an unsigned integer")
        }
        parser.fails("1", "two") {
            failure(1, "'two' is not an unsigned integer")
        }
        parser.fails("1", "two", "3") {
            failure(1, "'two' is not an unsigned integer")
        }
        parser.fails("one", "2", "three") {
            failure(0, "'one' is not an unsigned integer")
            failure(2, "'three' is not an unsigned integer")
        }
    }
}