package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class HelpAndChoiceOfCommandsTest : AbstractParseTest() {
    @Test
    fun `reports usage`() {
        val parser = app(
            "some_app",
            oneOf(
                params(command("start")) { fail() },
                params(command("stop")) { fail() }
            ),
            helpAction = true,
            completionAction = false
        )

        parser.usage {
            choice("some_app -h | --help", "Show some_app usage information")
            choice("some_app start")
            choice("some_app stop")
        }
        parser.options {
            option("-h, --help", "Show usage information")
        }
        parser.noPositionals()
    }

    @Test
    fun `shows help`() {
        val parser = app(
            "some_app",
            oneOf(
                params(command("start")) { fail() },
                params(command("stop")) { fail() }
            ),
            helpAction = true,
            completionAction = false
        )

        parser.help(
            """
USAGE:
  some_app -h | --help  Show some_app usage information
  some_app start
  some_app stop

OPTIONS:
  -h, --help  Show usage information

"""
        )
    }

    @Test
    fun `shows help when commands have summary`() {
        val parser = app(
            "some_app",
            oneOf(
                params(command("start"), summary = "Start the app") { fail() },
                params(command("stop"), summary = "Stop the app") { fail() }
            ),
            helpAction = true,
            completionAction = false
        )

        parser.help(
            """
USAGE:
  some_app -h | --help  Show some_app usage information
  some_app start  Start the app
  some_app stop  Stop the app

OPTIONS:
  -h, --help  Show usage information

"""
        )
    }
}