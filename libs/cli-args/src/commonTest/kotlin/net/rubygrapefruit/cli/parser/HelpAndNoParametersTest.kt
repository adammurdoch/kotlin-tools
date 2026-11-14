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
            choice("some_app -h | --help")
            choice("some_app")
        }
        app.option("-h, --help")
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
  some_app -h | --help
  some_app

OPTIONS:
  -h, --help

"""
        )
    }

    @Test
    fun `shows help when app has a summary`() {
        val parser = params { fail() }
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
  some_app -h | --help
  some_app

OPTIONS:
  -h, --help

"""
        )
    }
}