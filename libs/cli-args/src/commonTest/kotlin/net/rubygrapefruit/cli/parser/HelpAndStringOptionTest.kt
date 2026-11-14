package net.rubygrapefruit.cli.parser

import kotlin.test.*

class HelpAndStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses help with option with short name`() {
        val parser = option("x")
        val app = app(
            "some_app",
            params(parser) { OneParam(it) },
            helpAction = true,
            completionAction = false
        )

        assertIs<OneParam>(app.parse("-x", "value").get()).apply {
            assertEquals("value", value)
        }
        assertIs<OneParam>(app.parse().get()).apply {
            assertNull(value)
        }
        assertIs<HelpAction>(app.parse("--help").get())
    }

    @Test
    fun `reports usage for option with short name`() {
        val parser = option("x")
        val app = app(
            "some_app",
            params(parser) { fail() },
            helpAction = true,
            completionAction = false
        )

        app.usage {
            choice("some_app -h | --help")
            choice("some_app [-x <value>]")
        }
        app.options {
            option("-h, --help")
            option("-x <value>")
        }
    }

    @Test
    fun `shows help for option with short name`() {
        val parser = option("x")
        val app = app(
            "some_app",
            params(parser) { fail() },
            helpAction = true,
            completionAction = false
        )

        app.help(
            """
USAGE:
  some_app -h | --help  Shows this help message
  some_app [-x <value>]

OPTIONS:
  -h, --help
  -x <value>

"""
        )
    }
}