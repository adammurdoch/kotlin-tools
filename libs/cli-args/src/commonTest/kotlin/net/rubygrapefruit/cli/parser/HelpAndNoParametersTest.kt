package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.fail

class HelpAndNoParametersTest : AbstractParseTest() {
    @Test
    fun `parses help and no parameters`() {
        val parser = params { NoParams() }
        val app = app(
            "some_app",
            parser,
            helpAction = true,
            completionAction = false
        )

        assertIs<NoParams>(app.parse().get())
        assertIs<HelpAction>(app.parse("--help").get())
    }

    @Test
    fun `reports usage`() {
        val parser = params { fail() }
        val app = app(
            "some_app",
            parser,
            helpAction = true,
            completionAction = false
        )

        app.usage {
            choice("some_app -h | --help", "Show some_app usage information")
            choice("some_app")
        }
        app.option("-h, --help", "Show usage information")
    }

    @Test
    fun `shows help`() {
        val parser = params { fail() }
        val app = app(
            "some_app",
            parser,
            helpAction = true,
            completionAction = false
        )

        app.help(
            """
USAGE:
  some_app -h | --help  Show some_app usage information
  some_app

OPTIONS:
  -h, --help  Show usage information

"""
        )
    }

    @Test
    fun `shows help when app and action have a summary`() {
        val parser = params(summary = "Do the thing") { fail() }
        val app = app(
            "some_app",
            parser,
            helpAction = true,
            completionAction = false,
            summary = "Does something useful"
        )

        app.help(
            """
some_app - Does something useful

USAGE:
  some_app -h | --help  Show some_app usage information
  some_app  Do the thing

OPTIONS:
  -h, --help  Show usage information

"""
        )
    }
}