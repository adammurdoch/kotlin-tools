package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HelpAndPositionalTest : AbstractParseTest() {
    @Test
    fun `parses help with positional`() {
        val app = app(
            "some_app",
            params(positional("thing")) { OneParam(it) },
            helpAction = true,
            completionAction = false
        )

        assertIs<OneParam>(app.parse("value").get()).apply {
            assertEquals("value", value)
        }
        assertIs<HelpAction>(app.parse("--help").get())
    }

    @Test
    fun `reports usage`() {
        val app = app(
            "some_app",
            params(positional("thing")) { OneParam(it) },
            helpAction = true,
            completionAction = false
        )

        app.usage {
            choice("some_app -h | --help", "Show some_app usage information")
            choice("some_app <thing>")
        }
        app.option("-h, --help", "Show usage information")
    }

    @Test
    fun `shows help`() {
        val app = app(
            "some_app",
            params(positional("thing")) { OneParam(it) },
            helpAction = true,
            completionAction = false
        )

        app.help(
            """
USAGE:
  some_app -h | --help  Show some_app usage information
  some_app <thing>

PARAMETERS:
  <thing>

OPTIONS:
  -h, --help  Show usage information

"""
        )
    }

    @Test
    fun `shows help when parameter has summary`() {
        val app = app(
            "some_app",
            params(positional("thing", summary = "Thing to act on"), summary = "Do the thing") { OneParam(it) },
            helpAction = true,
            completionAction = false
        )

        app.help(
            """
USAGE:
  some_app -h | --help  Show some_app usage information
  some_app <thing>  Do the thing

PARAMETERS:
  <thing>  Thing to act on

OPTIONS:
  -h, --help  Show usage information

"""
        )
    }

}