package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class CommandAndChoiceOfOptionsTest : AbstractParseTest() {
    @Test
    fun `parses command with multiple choices of options`() {
        val parser = params(
            command("run"),
            oneOf(
                option("hostname", "h"),
                option("ip")
            ),
            option("root", "r").boolean().required(),
        ) { a, b -> listOf(a, b) }

        assertEquals(listOf("h", Unit), parser.parse("run", "-h", "h", "--root").get())
    }

    @Test
    fun `reports usage of command with multiple choices of options`() {
        val parser = params(
            command("run"),
            oneOf(
                option("hostname", "h"),
                option("ip")
            ),
            option("root", "r").boolean().required(),
        ) { a, b -> listOf(a, b) }

        assertEquals("run ([-h <value> | --hostname <value>] | [--ip <value>]) (-r | --root)", parser.usage)

        val options = parser.options
        assertEquals(3, options.size)
        assertEquals("-h <value>, --hostname <value>", options[0].optionUsage)
        assertEquals("--ip <value>", options[1].optionUsage)
        assertEquals("-r, --root", options[2].optionUsage)
    }
}