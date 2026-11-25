package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ZeroOrMoreUnsignedIntPositionalsTest : AbstractParseTest() {
    @Test
    fun `parses zero or more positional parameters`() {
        val parser = positional("a", unsignedInteger()).zeroOrMore()

        parser.edges {
            positional {
                matches("101") {
                    positional {
                        matches("102") {
                            positional {
                                matches("103") {
                                    positional()
                                    endOfInput(listOf(101, 102, 103))
                                }
                            }
                            endOfInput(listOf(101, 102))
                        }
                    }
                    endOfInput(listOf(101))
                }
            }
            endOfInput(emptyList<String>())
        }
    }

    @Test
    fun `argument must be an unsigned int`() {
        val parser = positional("a", unsignedInteger()).zeroOrMore()

        parser.fails("one") {
            failure(0, "Unexpected argument 'one'. Expected an unsigned int instead")
        }
        parser.fails("one", "2") {
            failure(0, "Unexpected argument 'one'. Expected an unsigned int instead")
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