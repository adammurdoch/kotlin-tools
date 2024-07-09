package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertEquals

class CliAppErrorReportingTest {
    private val formatter = BufferingFormatter()

    @Test
    fun `reports missing parameter for first positional`() {
        class Nested : CliApp("cmd") {
            val param by parameter("param", help = "parameter 1")
            val param2 by parameter("param2", help = "parameter 2")
            val param3 by parameter("no-help")
        }

        Nested().run(emptyList(), formatter)

        assertEquals(
            """
            Please provide a value for parameter 'param'.
            
            Usage: cmd <param> <param2> <no-help>
            
            Parameters:
              <param>  parameter 1
              <param2> parameter 2

        """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `reports missing action for first positional`() {
        class Nested : CliApp("cmd") {
            val action by actions {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(emptyList(), formatter)

        assertEquals(
            """
            Please specify an action to run.
            
            Usage: cmd <action>
            
            Available actions:
              one
              two run action two

        """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `reports missing action for subsequent positional`() {
        class Nested : CliApp("cmd") {
            val param by parameter("param")
            val action by actions {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(listOf("arg"), formatter)

        assertEquals(
            """
            Please specify an action to run.
            
            Usage: cmd <param> <action>
            
            Available actions:
              one
              two run action two

        """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `reports unknown action for first positional`() {
        class Nested : CliApp("cmd") {
            val action by actions {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(listOf("unknown"), formatter)

        assertEquals(
            """
            Unknown action: unknown
            
            Usage: cmd <action>
            
            Available actions:
              one
              two run action two

        """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `reports unknown action for subsequent positional`() {
        class Nested : CliApp("cmd") {
            val param by parameter("param")
            val action by actions {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(listOf("arg", "unknown"), formatter)

        assertEquals(
            """
            Unknown action: unknown
            
            Usage: cmd <param> <action>
            
            Available actions:
              one
              two run action two

        """.trimIndent(), formatter.text
        )
    }
}