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
            }
            aborts("Option -a not provided")
            nonPositional {
                matches("-b", "two") {
                    endOfInput("two")
                }
            }
            aborts("Option -b not provided")
            nonPositional {
                matches("--long", "three") {
                    endOfInput("three")
                }
            }
            aborts("Option --long not provided")
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
            failure(0, "Option -a not provided")
//            failure(0, "Option -b not provided")
//            failure(0, "Option --long not provided")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Option -a not provided")
        }
    }
}