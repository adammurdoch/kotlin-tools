package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfCommandOrNoParametersTest : AbstractParseTest() {
    @Test
    fun `parses one of command or no parameters`() {
        val parser = oneOf(
            params(command("one")) { "command" },
            params { "none" }
        )

        assertEquals("command", parser.parse("one").get())
        assertEquals("none", parser.parse().get())
    }

    @Test
    fun `parses one of no parameters or command`() {
        val parser = oneOf(
            params { "none" },
            params(command("one")) { "command" }
        )

        assertEquals("command", parser.parse("one").get())
        assertEquals("none", parser.parse().get())
    }
}