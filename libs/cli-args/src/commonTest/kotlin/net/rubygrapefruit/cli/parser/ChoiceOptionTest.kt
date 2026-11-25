package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ChoiceOptionTest: AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x", oneOf("one" to 1, "two" to 2))

        parser.edges {
            nonPositional {
                matches("-x", "one") {
                    endOfInput(1)
                }
                matches("-x", "two") {
                    endOfInput(2)
                }
                doesNotMatch("unknown")
                doesNotMatch("--other")
            }
            endOfInput(null)
        }
    }

    @Test
    fun `value must be one of the choices`() {
        val parser = option("x", oneOf("one" to 1, "two" to 2))

        parser.fails("-x", "value") {
            failure(1, "Unexpected value 'value' for option -x. Expected one of 'one', 'two' instead")
        }
        parser.fails("-x", "") {
            failure(1, "Unexpected value '' for option -x. Expected one of 'one', 'two' instead")
        }
    }
}