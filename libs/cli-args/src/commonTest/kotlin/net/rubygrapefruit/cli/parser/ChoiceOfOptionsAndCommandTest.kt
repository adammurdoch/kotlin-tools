package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfOptionsAndCommandTest : AbstractParseTest() {
    @Test
    fun `parses choice of mapped option and command`() {
        val parser = oneOf(
            params(option("help", "h").boolean().required()) { "help" },
            params(command("run")) { "run" }
        )

        assertEquals("help", parser.parse("-h").get())
        assertEquals("help", parser.parse("--help").get())
        assertEquals("run", parser.parse("run").get())
    }

    @Test
    fun `reports usage of choice of mapped option and command`() {
        val parser = oneOf(
            params(option("help", "h").boolean().required()) { "help" },
            params(command("run")) { "run" }
        )

        assertEquals(
            """-h | --help
run""", parser.usage
        )
    }
}