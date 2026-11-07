package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x")

        assertEquals("value", parser.parse("-x", "value").get())
        assertEquals("", parser.parse("-x", "").get())
        assertNull(parser.parse().get())
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

        assertEquals("value", parser.parse("-x", "value").get())
        assertEquals("", parser.parse("-x", "").get())

        assertEquals("value", parser.parse("--long-x", "value").get())
        assertEquals("value", parser.parse("-y", "value").get())
        assertEquals("value", parser.parse("--option-x", "value").get())

        assertNull(parser.parse().get())
    }

    @Test
    fun `reports usage of option with short name`() {
        val parser = option("x")

        assertEquals("[-x <value>]", parser.usage)
    }

    @Test
    fun `reports usage of option with long name`() {
        val parser = option("an-option")

        assertEquals("[--an-option <value>]", parser.usage)
    }

    @Test
    fun `reports usage of option with multiple names`() {
        val parser = option("option-x", "x", "aka", "a")

        assertEquals("[-a <value> | -x <value> | --aka <value> | --option-x <value>]", parser.usage)
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

        parser.fails("--x", "value") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("-X", "value") {
            failure(0, "Unexpected option '-X'. Try '-x' instead")
        }
        parser.fails("--X", "value") {
            failure(0, "Unexpected option '--X'. Try '-x' instead")
        }

        parser.fails("--x") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("-x", "value", "--x") {
            failure(2, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("--x", "-x", "value") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
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
    fun `option must have value`() {
        val parser = option("x")

        parser.fails("-x") {
            failure(1, "No value provided for option -x")
        }

        parser.fails("-x", "-unknown") {
            failure(1, "No value provided for option -x")
            failure(1, "Unexpected option '-unknown'")
        }

        parser.fails("-unknown", "-x") {
            failure(0, "Unexpected option '-unknown'")
            failure(2, "No value provided for option -x")
        }
    }

    @Test
    fun `option cannot appear twice`() {
        val parser = option("x")

        parser.fails("-x", "value", "-x", "value") {
            failure(2, "Option -x can only appear once")
        }
        parser.fails("-x", "value", "-x") {
            failure(2, "Option -x can only appear once")
        }
        parser.fails("-x", "-x", "value") {
            failure(1, "No value provided for option -x")
            failure(1, "Option -x can only appear once")
        }
        parser.fails("-x", "-x") {
            failure(1, "No value provided for option -x")
            failure(1, "Option -x can only appear once")
        }
    }
}