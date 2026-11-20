package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class RequiredStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x").required()

        parser.edges {
            nonPositional {
                matches("-x", "value") {
                    endOfInput("value")
                }
                matches("-x", "") {
                    endOfInput("")
                }
                doesNotMatch("unknown")
                doesNotMatch("--other")
            }
            expectingOption("-x")
        }
    }

    @Test
    fun `parses option with long name`() {
        val parser = option("some-option").required()

        assertEquals("value", parser.parse("--some-option", "value").get())
    }

    @Test
    fun `parses option with multiple names`() {
        val parser = option("some-option", "x").required()

        parser.edges {
            nonPositional {
                matches("-x", "value") {
                    endOfInput("value")
                }
                matches("--some-option", "value") {
                    endOfInput("value")
                }
            }
            expectingOption("--some-option")
        }
    }

    @Test
    fun `reports usage of option with short name`() {
        val parser = option("x").required()

        parser.usage("-x <value>")
        parser.option("-x <value>")
        parser.noPositionals()
    }

    @Test
    fun `can provide summary`() {
        val parser = option("x", summary = "Something useful").required()

        parser.usage("-x <value>")
        parser.option("-x <value>", "Something useful")
        parser.noPositionals()
    }

    @Test
    fun `can provide value name`() {
        val parser = option("x", valueName = "thing").required()

        parser.usage("-x <thing>")
        parser.option("-x <thing>")
        parser.noPositionals()
    }

    @Test
    fun `option with short name must be present`() {
        val parser = option("x").required()

        parser.fails {
            expectedOption(0, "-x")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected option -x instead")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'. Expected option -x instead")
        }
    }

    @Test
    fun `option with multiple names must be present`() {
        val parser = option("some-flag", "x").required()

        parser.fails {
            expectedOption(0, "--some-flag")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected option --some-flag instead")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'. Expected option --some-flag instead")
        }
    }

    @Test
    fun `option with multiple names must be well formed`() {
        val parser = option("long", "x").required()

        parser.fails("-long", "value") {
            failure(0, "Unexpected option '-long'. Try '--long' instead")
        }
        parser.fails("--Long", "value") {
            failure(0, "Unexpected option '--Long'. Try '--long' instead")
        }
        parser.fails("-X", "value") {
            failure(0, "Unexpected option '-X'. Try '-x' instead")
        }
        parser.fails("--x") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
    }

    @Test
    fun `option must have value`() {
        val parser = option("x", "long").required()

        parser.edges {
            nonPositional {
                fails("-x", index = 1, message = "No value provided for option -x")
                fails("--long", index = 1, message = "No value provided for option --long")
            }
            expectingOption("--long")
        }
    }

    @Test
    fun `option with multiple names cannot appear twice`() {
        val parser = option("long", "x", "an-option").required()

        parser.fails("-x", "value", "-x", "value") {
            failure(2, "Option -x can only appear once")
        }
        parser.fails("-x", "value", "--long", "value") {
            failure(2, "Option --long cannot be used with -x")
        }
        parser.fails("--an-option", "value", "--long", "value") {
            failure(2, "Option --long cannot be used with --an-option")
        }
    }
}