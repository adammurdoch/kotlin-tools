package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

class AppWithChoiceOfOptionOrCommandTest : AbstractParseTest() {
    @Test
    fun `parses choice of option or command`() {
        val choices = oneOf(
            params(option("help").boolean().required()) { OneParam("help") },
            params(command("run")) { OneParam("run") }
        )
        val parser = app("some_app", choices, helpAction = false, completionAction = false)

        parser.parse("--help").get().apply {
            assertIs<OneParam>(this)
            assertEquals("help", value)
        }
        parser.parse("run").get().apply {
            assertIs<OneParam>(this)
            assertEquals("run", value)
        }

        parser.edges {
            nonPositional {
                matches("--help") {
                    endOfInput(OneParam("help"))
                }
            }
            expectingOption("--help")
            positional {
                matches("run") {
                    endOfInput(OneParam("run"))
                }
            }
            expectingCommand("run")
        }
    }

    @Test
    fun `reports app usage`() {
        val choices = oneOf(
            params(option("help").boolean().required()) { NoParams() },
            params(command("run")) { NoParams() }
        )
        val parser = app("some_app", choices, helpAction = false, completionAction = false)

        parser.usage {
            choice("some_app --help")
            choice("some_app run")
        }
        parser.options {
            option("--help")
        }
        parser.noPositionals()
    }

    @Test
    fun `option or command must be provided`() {
        val choices = oneOf(
            params(option("help").boolean().required()) { fail() },
            params(command("run")) { fail() }
        )
        val parser = app("some_app", choices, helpAction = false, completionAction = false)

        parser.fails {
            failure(0, "Expected option --help or 'run'")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'. Expected option --help or 'run' instead")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'. Expected option --help or 'run' instead")
        }
    }
}