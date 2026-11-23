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
            failure(0, "'one' is not an unsigned integer")
        }
        parser.fails("one", "2") {
            failure(0, "'one' is not an unsigned integer")
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