package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CommandTest : AbstractParseTest() {
    @Test
    fun `parses command`() {
        val parser = command("help")

        assertEquals(Unit, parser.parse("help").get())
    }

    @Test
    fun `name must be well formed`() {
        val candidates = listOf(
            "" to "Command name cannot be empty",
            "-"  to "Command name '-' cannot start with '-'",
            "-x" to "Command name '-x' cannot start with '-'",
            "two words" to "Command name 'two words' cannot contain whitespace"
        )

        for ((name, expected) in candidates) {
            assertFailsWith<IllegalArgumentException> {
                command(name)
            }.apply {
                assertEquals(expected, message)
            }
        }
    }

    @Test
    fun composes() {
        val parser = command("help")

        parser.edges {
            positional {
                matches("help") {
                    endOfInput(Unit)
                }
                doesNotMatch("other")
                doesNotMatch("-x")
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = command("help")

        parser.usage("help")
        parser.noOptions()
        parser.noPositionals()
    }

    @Test
    fun `cannot include other input values`() {
        val parser = command("help")

       parser.fails("HELP") {
            failure(0, "Unexpected argument 'HELP'. Expected 'help' instead")
        }
        parser.fails("help", "HELP") {
            failure(1, "Unexpected argument 'HELP'")
        }
        parser.fails("HELP", "help") {
            failure(0, "Unexpected argument 'HELP'")
        }
    }

    @Test
    fun `command must be provided`() {
        val parser = command("help")

        parser.fails {
            failure(0, "Expected 'help'")
        }
        parser.fails("other") {
            failure(0, "Unexpected argument 'other'. Expected 'help' instead")
        }
        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
            failure(1, "Expected 'help'")
        }
    }

    @Test
    fun `command cannot appear more than once`() {
        val parser = command("help")

        parser.fails("help", "help") {
            failure(1, "Unexpected argument 'help'")
        }
        parser.fails("help", "help", "help") {
            failure(1, "Unexpected argument 'help'")
            failure(2, "Unexpected argument 'help'")
        }
    }
}