package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class HelpActionTest : AbstractParseTest() {
    @Test
    fun `parses choice of help or command`() {
        val parser = app("some_app", params(command("run")) { DoNothing }).withHelp()

        assertSame(DoNothing, parser.parse("run").get())
        assertIs<HelpAction>(parser.parse("--help").get())
        assertIs<HelpAction>(parser.parse("-h").get())
    }

    @Test
    fun `reports usage of choice of help or command`() {
        val parser = app("some_app", params(command("run")) { DoNothing }).withHelp()

        assertEquals(
            """some_app -h | --help
some_app run""", parser.usage
        )
    }
}