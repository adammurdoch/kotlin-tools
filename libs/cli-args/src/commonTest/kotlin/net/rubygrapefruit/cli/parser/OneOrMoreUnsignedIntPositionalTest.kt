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
}