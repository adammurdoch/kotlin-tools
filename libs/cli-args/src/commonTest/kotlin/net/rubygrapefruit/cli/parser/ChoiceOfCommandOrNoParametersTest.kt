package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.fail

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
                expectingCommand("one")
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
                expectingCommand("one")
            }
        }
    }

    @Test
    fun `cannot include extra inputs`() {
        val parser = oneOf(
            params(command("one")) { fail() },
            params { fail() }
        )

        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'. Expected 'one' instead")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected 'one' instead")
        }
        parser.fails("one", "unknown") {
            failure(1, "Unexpected argument 'unknown'")
        }
        parser.fails("one", "one") {
            failure(1, "Unexpected argument 'one'")
        }
        parser.fails("unknown", "one") {
            failure(0, "Unexpected argument 'unknown'")
        }
        parser.fails("one", "-unknown") {
            failure(1, "Unexpected option '-unknown'")
        }
    }
}