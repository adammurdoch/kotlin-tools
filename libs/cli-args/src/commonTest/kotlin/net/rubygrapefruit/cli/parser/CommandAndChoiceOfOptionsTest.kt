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

        parser.options {
            option("-h <value>, --hostname <value>")
            option("--ip <value>")
            option("-r, --root")
        }
    }
}