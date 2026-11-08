package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class HelpActionTest : AbstractParseTest() {
    private object DoNothing : Runnable {
        override fun run() {
        }
    }

    @Test
    fun `parses choice of help or command`() {
        val parser = withHelp(params(command("run")) { DoNothing })

        assertSame(DoNothing, parser.parse("run").get())
        assertIs<HelpAction>(parser.parse("--help").get())
        assertIs<HelpAction>(parser.parse("-h").get())
    }

    @Test
    fun `reports usage of choice of help or command`() {
        val parser = withHelp(params(command("run")) { DoNothing })

        assertEquals(
            """-h | --help
run""", parser.usage)
    }
}