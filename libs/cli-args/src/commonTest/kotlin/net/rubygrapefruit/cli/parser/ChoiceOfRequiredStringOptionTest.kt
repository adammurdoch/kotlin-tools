package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ChoiceOfRequiredStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses one of string options`() {
        val parser = oneOf(
            option("a").required(),
            option("b").required(),
            option("long").required()
        )

        parser.edges {
            nonPositional {
                matches("-a", "one") {
                    endOfInput("one")
                }
                expectingOption("-a")
            }
            nonPositional {
                matches("-b", "two") {
                    endOfInput("two")
                }
                expectingOption("-b")
            }
            nonPositional {
                matches("--long", "three") {
                    endOfInput("three")
                }
                expectingOption("--long")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = oneOf(
            option("a").required(),
            option("b").required(),
            option("long").required()
        )

        parser.usage {
            choice("-a <value>")
            choice("-b <value>")
            choice("--long <value>")
        }

        parser.options {
            option("-a <value>")
            option("-b <value>")
            option("--long <value>")
        }

        parser.noPositionals()
    }

    @Test
    fun `option must be provided`() {
        val parser = oneOf(
            option("a").required(),
            option("b").required(),
            option("long").required()
        )

        parser.fails {
            failure(0, "Expected option -a, option -b or option --long")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected option -a, option -b or option --long instead")
        }
    }
}