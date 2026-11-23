package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class UnsignedIntOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x", unsignedInteger())

        parser.edges {
            nonPositional {
                matches("-x", "12") {
                    endOfInput(12)
                }
                matches("-x", "0") {
                    endOfInput(0)
                }
                doesNotMatch("unknown")
                doesNotMatch("--other")
            }
            endOfInput(null)
        }
    }

    @Test
    fun `value must be an unsigned int`() {
        val parser = option("x", unsignedInteger())

        parser.fails("-x", "value") {
            failure(1, "'value' is not an unsigned integer")
        }
        parser.fails("-x", "") {
            failure(1, "'' is not an unsigned integer")
        }
        parser.fails("-x", "-12") {
            failure(1, "No value provided for option -x")
            failure(1, "Unexpected option '-12'")
        }
        parser.fails("-x", "0.45") {
            failure(1, "'0.45' is not an unsigned integer")
        }
    }
}