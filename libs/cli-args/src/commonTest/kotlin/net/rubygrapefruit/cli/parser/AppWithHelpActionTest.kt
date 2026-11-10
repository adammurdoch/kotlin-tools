package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AppWithHelpActionTest : AbstractParseTest() {
    @Test
    fun `shows help for option with short name`() {
        val parser = option("x")

        val app = app("some_app", params(parser) { OneParam(it) }).withHelp()
        val action = assertIs<HelpAction>(app.parse("--help").get())
        val output = BufferingOutput()
        action.run(output)

        assertEquals(
            """
USAGE:
  some_app -h | --help
  some_app [-x <value>]
""", output.text
        )
    }
}