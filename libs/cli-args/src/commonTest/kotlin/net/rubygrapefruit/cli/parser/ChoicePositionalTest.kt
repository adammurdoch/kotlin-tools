package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ChoicePositionalTest : AbstractParseTest() {
    @Test
    fun `parses positional`() {
        val parser = positional("param", oneOf("one" to 1, "two" to 2))

        parser.edges {
            positional {
                matches("one") {
                    endOfInput(1)
                }
                matches("two") {
                    endOfInput(2)
                }
                doesNotMatch("-x")
                doesNotMatch("other")
                expecting("'one'", "'two'")
            }
        }
    }

    @Test
    fun `argument must be one of the choices`() {
        val parser = positional("param", oneOf("one" to 1, "two" to 2))

        parser.fails("string") {
            failure(0, "Unexpected argument 'string'. Expected 'one' or 'two' instead")
        }
        parser.fails("") {
            failure(0, "Unexpected argument ''. Expected 'one' or 'two' instead")
        }
        parser.fails("One") {
            failure(0, "Unexpected argument 'One'. Expected 'one' or 'two' instead")
        }
        parser.fails("TWO") {
            failure(0, "Unexpected argument 'TWO'. Expected 'one' or 'two' instead")
        }
    }
}