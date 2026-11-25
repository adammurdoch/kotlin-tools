package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class UnsignedIntPositionalTest : AbstractParseTest() {
    @Test
    fun `parses positional`() {
        val parser = positional("param", unsignedInteger())

        parser.edges {
            positional {
                matches("12") {
                    endOfInput(12)
                }
                matches("0") {
                    endOfInput(0)
                }
                matches(Int.MAX_VALUE.toString()) {
                    endOfInput(Int.MAX_VALUE)
                }
                doesNotMatch("-x")
                expecting("an unsigned int")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = positional("param", unsignedInteger())

        parser.usage("<param>")
        parser.noOptions()
        parser.positional("<param>")
    }

    @Test
    fun `argument must be provided`() {
        val parser = positional("param", unsignedInteger())

        parser.fails {
            expectedPositional(0, "param")
        }
    }

    @Test
    fun `argument must be an unsigned integer`() {
        val parser = positional("param", unsignedInteger())

        parser.fails("string") {
            failure(0, "Unexpected argument 'string'. Expected an unsigned int instead")
        }
        parser.fails("") {
            failure(0, "Unexpected argument ''. Expected an unsigned int instead")
        }
        parser.fails("-12") {
            failure(0, "Unexpected option '-12'. Expected an unsigned int instead")
        }
        parser.fails("1.2") {
            failure(0, "Unexpected argument '1.2'. Expected an unsigned int instead")
        }
        val overflow = (Int.MAX_VALUE.toLong() + 1).toString()
        parser.fails(overflow) {
            failure(0, "Unexpected argument '$overflow'. Expected an unsigned int instead")
        }
    }
}