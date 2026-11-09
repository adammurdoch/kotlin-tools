package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class AppWithHelpActionTest : AbstractParseTest() {
    @Test
    fun `parses choice of help or command`() {
        val start = NoParams()
        val stop = NoParams()
        val parser = app(
            "some_app",
            oneOf(
                params(command("start")) { start },
                params(command("stop")) { stop }
            )
        ).withHelp()

        assertSame(start, parser.parse("start").get())
        assertSame(stop, parser.parse("stop").get())
        assertIs<HelpAction>(parser.parse("--help").get())
        assertIs<HelpAction>(parser.parse("-h").get())
    }

    @Test
    fun `reports usage of choice of help or command`() {
        val start = NoParams()
        val stop = NoParams()
        val parser = app(
            "some_app",
            oneOf(
                params(command("start")) { start },
                params(command("stop")) { stop }
            )
        ).withHelp()

        assertEquals(
            """some_app -h | --help
some_app start
some_app stop""", parser.usage
        )
    }

    @Test
    fun `shows help for option with short name`() {
        val parser = option("x")

        val app = app("some_app", params(parser) { OneParam(it) }).withHelp()
        val action = assertIs<HelpAction>(app.parse("--help").get())
        val output = BufferingOutput()
        action.run(output)

        assertEquals(
            """
-h | --help
[-x <value>]
""", output.text
        )
    }
}