package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

class HelpAndChoiceOfCommands : AbstractParseTest() {
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
            choice("some_app -h | --help")
            choice("some_app start")
            choice("some_app stop")
        }
        parser.options {
            option("-h, --help")
        }
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
  some_app -h | --help
  some_app start
  some_app stop

OPTIONS:
  -h, --help

"""
        )
    }
}