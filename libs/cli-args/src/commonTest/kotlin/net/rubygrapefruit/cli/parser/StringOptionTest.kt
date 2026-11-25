package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x")

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
            endOfInput(null)
        }
    }

    @Test
    fun `parses option with long name`() {
        val parser = option("an-option")

        assertEquals("value", parser.parse("--an-option", "value").get())
        assertEquals("", parser.parse("--an-option", "").get())
        assertNull(parser.parse().get())
    }

    @Test
    fun `parses option with multiple names`() {
        val parser = option("x", "long-x", "y", "option-x")

        parser.edges {
            nonPositional {
                matches("-x", "value") {
                    endOfInput("value")
                }
                matches("-x", "") {
                    endOfInput("")
                }
                matches("--long-x", "value") {
                    endOfInput("value")
                }
                matches("-y", "value") {
                    endOfInput("value")
                }
                matches("--option-x", "value") {
                    endOfInput("value")
                }
                doesNotMatch("unknown")
                doesNotMatch("--other")
            }
            endOfInput(null)
        }
    }

    @Test
    fun `reports usage of option with short name`() {
        val parser = option("x")

        parser.usage("[-x <value>]")
        parser.option("-x <value>")
        parser.noPositionals()
    }

    @Test
    fun `reports usage of option with long name`() {
        val parser = option("an-option")

        parser.usage("[--an-option <value>]")
        parser.option("--an-option <value>")
        parser.noPositionals()
    }

    @Test
    fun `reports usage of option with multiple names`() {
        val parser = option("option-x", "x", "aka", "a")

        parser.usage("[-a <value> | -x <value> | --aka <value> | --option-x <value>]")
        parser.option("-a <value>, -x <value>, --aka <value>, --option-x <value>")
        parser.noPositionals()
    }

    @Test
    fun `can provide summary`() {
        val parser = option("x", summary = "Something useful")

        parser.usage("[-x <value>]")
        parser.option("-x <value>", "Something useful")
        parser.noPositionals()
    }

    @Test
    fun `can provide value name`() {
        val parser = option("x", valueName = "some thing")

        parser.usage("[-x <some thing>]")
        parser.option("-x <some thing>")
        parser.noPositionals()
    }

    @Test
    fun `cannot include other input values`() {
        val parser = option("x")

        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
        }
        parser.fails("value") {
            failure(0, "Unexpected argument 'value'")
        }
        parser.fails("") {
            failure(0, "Unexpected argument ''")
        }
        parser.fails("-unknown", "value") {
            failure(0, "Unexpected option '-unknown'")
            failure(1, "Unexpected argument 'value'")
        }
        parser.fails("value", "--unknown") {
            failure(0, "Unexpected argument 'value'")
            failure(1, "Unexpected option '--unknown'")
        }

        parser.fails("-x", "value", "-unknown") {
            failure(2, "Unexpected option '-unknown'")
        }
        parser.fails("-x", "value", "value") {
            failure(2, "Unexpected argument 'value'")
        }
    }

    @Test
    fun `option with short name must be well formed`() {
        val parser = option("x")

        parser.fails("--x", "-x", "value") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }

        parser.edges {
            nonPositional {
                matches("-x", "value") {
                    endOfInput("value") {
                        doesNotMatch("--x", "value", count = 2, message = "Unexpected option '--x'. Try '-x' instead")
                    }
                }
                doesNotMatch("--x", "value", count = 2, message = "Unexpected option '--x'. Try '-x' instead")
                doesNotMatch("--x", message = "Unexpected option '--x'. Try '-x' instead")
                doesNotMatch("-X", "value", count = 2, message = "Unexpected option '-X'. Try '-x' instead")
                doesNotMatch("--X", "value", count = 2, message = "Unexpected option '--X'. Try '-x' instead")
            }
            endOfInput(null)
        }
    }

    @Test
    fun `option with long name must be well formed`() {
        val parser = option("an-option")

        parser.fails("-an-option", "value") {
            failure(0, "Unexpected option '-an-option'. Try '--an-option' instead")
        }
        parser.fails("--An-Option", "value") {
            failure(0, "Unexpected option '--An-Option'. Try '--an-option' instead")
        }
        parser.fails("-An-Option", "value") {
            failure(0, "Unexpected option '-An-Option'. Try '--an-option' instead")
        }

        parser.fails("-an-option") {
            failure(0, "Unexpected option '-an-option'. Try '--an-option' instead")
        }
        parser.fails("--an-option", "value", "-an-option") {
            failure(2, "Unexpected option '-an-option'. Try '--an-option' instead")
        }
    }

    @Test
    fun `option with multiple names must be well formed`() {
        val parser = option("long", "x", "an-option")

        parser.fails("--x", "value") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("-LONG", "value") {
            failure(0, "Unexpected option '-LONG'. Try '--long' instead")
        }
        parser.fails("-an-option", "value") {
            failure(0, "Unexpected option '-an-option'. Try '--an-option' instead")
        }
    }

    @Test
    fun `option must have value`() {
        val parser = option("x", "long")

        parser.fails("-x", "-unknown") {
            failure(1, "No value provided for option -x")
            failure(1, "Unexpected option '-unknown'")
        }
        parser.fails("-x", "-unknown", "value") {
            failure(1, "No value provided for option -x")
            failure(1, "Unexpected option '-unknown'")
            failure(2, "Unexpected argument 'value'")
        }

        parser.fails("-unknown", "-x") {
            failure(0, "Unexpected option '-unknown'")
            failure(2, "No value provided for option -x")
        }

        parser.edges {
            nonPositional {
                fails("-x", index = 1, message = "No value provided for option -x")
                fails("--long", index = 1, message = "No value provided for option --long")
            }
            endOfInput(null)
        }
    }

    @Test
    fun `option cannot appear twice`() {
        val parser = option("x")

        parser.edges {
            nonPositional {
                matches("-x", "value") {
                    endOfInput("value") {
                        doesNotMatch("-x", "value", count = 2, message = "Option -x can only appear once")
                        doesNotMatch("-x", message = "Option -x can only appear once")
                    }
                }
                fails("-x", index = 1, message = "No value provided for option -x") {
                    doesNotMatch("-x", "value", count = 2, message = "Option -x can only appear once")
                    doesNotMatch("-x", message = "Option -x can only appear once")
                }
            }
            endOfInput(null)
        }
    }

    @Test
    fun `option with multiple names cannot appear twice`() {
        val parser = option("long", "x", "an-option")

        parser.fails("--an-option", "value", "--long", "value") {
            failure(2, "Option --long cannot be used with --an-option")
        }
        parser.fails("--an-option", "value", "-x", "--long") {
            failure(2, "Option -x cannot be used with --an-option")
            failure(3, "Option --long cannot be used with --an-option")
        }

        parser.edges {
            nonPositional {
                matches("-x", "value") {
                    endOfInput("value") {
                        doesNotMatch("-x", "value", count = 2, message = "Option -x can only appear once")
                        doesNotMatch("-x", message = "Option -x can only appear once")
                        doesNotMatch("--long", message = "Option --long cannot be used with -x")
                        doesNotMatch("--long", "value", count = 2, message = "Option --long cannot be used with -x")
                    }
                }
                matches("--long", "value") {
                    endOfInput("value") {
                        doesNotMatch("--long", "value", count = 2, message = "Option --long can only appear once")
                        doesNotMatch("-x", message = "Option -x cannot be used with --long")
                    }
                }
            }
            endOfInput(null)
        }
    }
}