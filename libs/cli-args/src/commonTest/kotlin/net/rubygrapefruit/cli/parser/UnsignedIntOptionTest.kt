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
    fun `reports usage of option with short name`() {
        val parser = option("x", unsignedInteger())

        parser.usage("[-x <value>]")
        parser.option("-x <value>")
        parser.noPositionals()
    }

    @Test
    fun `value must be an unsigned int`() {
        val parser = option("x", unsignedInteger())

        parser.fails("-x", "value") {
            failure(1, "Unexpected argument 'value' for option -x. Expected an unsigned int instead")
        }
        parser.fails("-x", "") {
            failure(1, "Unexpected argument '' for option -x. Expected an unsigned int instead")
        }
        parser.fails("-x", "-12") {
            failure(1, "No value provided for option -x")
            failure(1, "Unexpected option '-12'")
        }
        parser.fails("-x", "0.45") {
            failure(1, "Unexpected argument '0.45' for option -x. Expected an unsigned int instead")
        }
    }
}