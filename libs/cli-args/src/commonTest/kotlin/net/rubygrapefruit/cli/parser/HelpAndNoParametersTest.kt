package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.fail

class HelpAndNoParametersTest : AbstractParseTest() {
    @Test
    fun `parses help and no parameters`() {
        val app = app(
            "some_app",
            params { NoParams() },
            helpAction = true,
            completionAction = false
        )

        assertIs<NoParams>(app.parse().get())
        assertIs<HelpAction>(app.parse("--help").get())
    }

    @Test
    fun `reports usage`() {
        val app = app(
            "some_app",
            params { fail() },
            helpAction = true,
            completionAction = false
        )

        app.usage {
            choice("some_app -h | --help", "Show some_app usage information")
            choice("some_app")
        }
        app.option("-h, --help", "Show usage information")
        app.noPositionals()
    }

    @Test
    fun `reports usage when app and action have a summary`() {
        val app = app(
            "some_app",
            params(summary = "Do the thing") { fail() },
            helpAction = true,
            completionAction = false,
            summary = "Does something useful"
        )

        app.usage {
            choice("some_app -h | --help", "Show some_app usage information")
            choice("some_app", "Do the thing")
        }
        app.option("-h, --help", "Show usage information")
        app.noPositionals()
    }

    @Test
    fun `shows help`() {
        val app = app(
            "some_app",
            params { fail() },
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
        val app = app(
            "some_app",
            params(summary = "Do the thing") { fail() },
            helpAction = true,
            completionAction = false,
            summary = "Does something useful"
        )

        app.help(
            """
some_app - Does something useful

USAGE:
  some_app -h | --help  Show some_app usage information
  some_app              Do the thing

OPTIONS:
  -h, --help  Show usage information

"""
        )
    }
}