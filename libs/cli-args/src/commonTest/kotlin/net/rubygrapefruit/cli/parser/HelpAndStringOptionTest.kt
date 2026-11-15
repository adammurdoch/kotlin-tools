package net.rubygrapefruit.cli.parser

import kotlin.test.*

class HelpAndStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses help with option with short name`() {
        val app = app(
            "some_app",
            params(option("x")) { OneParam(it) },
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
        val app = app(
            "some_app",
            params(option("x")) { fail() },
            helpAction = true,
            completionAction = false
        )

        app.usage {
            choice("some_app -h | --help", "Show some_app usage information")
            choice("some_app [-x <value>]")
        }
        app.options {
            option("-h, --help", "Show usage information")
            option("-x <value>")
        }
    }

    @Test
    fun `shows help for option with short name`() {
        val app = app(
            "some_app",
            params(option("x")) { fail() },
            helpAction = true,
            completionAction = false
        )

        app.help(
            """
USAGE:
  some_app -h | --help  Show some_app usage information
  some_app [-x <value>]

OPTIONS:
  -h, --help  Show usage information
  -x <value>

"""
        )
    }

    @Test
    fun `shows help for option with short name and summary`() {
        val app = app(
            "some_app",
            params(option("x", summary = "The value to thing"), summary = "Do the thing") { fail() },
            helpAction = true,
            completionAction = false
        )

        app.help(
            """
USAGE:
  some_app -h | --help  Show some_app usage information
  some_app [-x <value>]  Do the thing

OPTIONS:
  -h, --help  Show usage information
  -x <value>  The value to thing

"""
        )
    }
}