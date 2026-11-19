package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class ChoiceOfCommandOrNoParametersTest : AbstractParseTest() {
    @Test
    fun `parses one of command or no parameters`() {
        val parser = oneOf(
            params(command("one")) { "command" },
            params { "none" }
        )

        parser.edges {
            positional {
                matches("one") {
                    endOfInput("command")
                }
            }
            endOfInput("none")
        }
    }

    @Test
    fun `parses one of no parameters or command`() {
        val parser = oneOf(
            params { "none" },
            params(command("one")) { "command" }
        )

        parser.edges {
            endOfInput("none")
            positional {
                matches("one") {
                    endOfInput("command")
                }
            }
        }
    }
}