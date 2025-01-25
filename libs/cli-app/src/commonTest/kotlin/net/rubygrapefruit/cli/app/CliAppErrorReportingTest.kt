package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertEquals

class CliAppErrorReportingTest {
    private val formatter = BufferingFormatter()

    @Test
    fun `reports unknown option`() {
        class App : CliApp("cmd") {
            val flag by flag("flag")
        }

        App().run(listOf("-x", "42"), formatter)
        hasUsageMessage(
            """
            Unknown option: -x
        """
        )
    }

    @Test
    fun `cannot use help action when app has no nested actions`() {
        class App : CliApp("cmd") {
            val flag by flag("flag")
        }

        App().run(listOf("help"), formatter)
        hasUsageMessage(
            """
            Unknown parameter: help
        """
        )
    }

    @Test
    fun `reports missing parameter for first positional`() {
        class App : CliApp("cmd") {
            val param by parameter("param", help = "parameter 1")
            val param2 by parameter("param2", help = "parameter 2")
            val param3 by parameter("no-help")
        }

        App().run(emptyList(), formatter)
        hasUsageMessage(
            """
            Please provide a value for parameter 'param'.
            
            Usage: cmd <param> <param2> <no-help>
            
            Parameters:
              <param>  parameter 1
              <param2> parameter 2
        """
        )
    }

    @Test
    fun `reports missing parameter for first positional of nested action`() {
        class Sub : Action() {
            val param by parameter("param2", help = "parameter 2")
        }

        class App : CliApp("cmd") {
            val param by parameter("param1", help = "parameter 1")
            val action by action {
                action(Sub(), "action")
            }
        }

        App().run(listOf("arg1", "action"), formatter)

        hasUsageMessage(
            """
            Please provide a value for parameter 'param2'.
            
            Usage: cmd <param1> action <param2>
            
            Parameters:
              <param1> parameter 1
              <param2> parameter 2
        """
        )
    }

    @Test
    fun `reports missing parameter for first positional of default nested action`() {
        class Sub : Action() {
            val param by parameter("param2", help = "parameter 2")
        }

        class App : CliApp("cmd") {
            val param by parameter("param1", help = "parameter 1")
            val action by action {
                action(Sub(), "action")
                action(Sub())
            }
        }

        App().run(listOf("arg1"), formatter)

        hasUsageMessage(
            """
            Please provide a value for parameter 'param2'.
            
            Usage: cmd <param1> <param2>
            
            Parameters:
              <param1> parameter 1
              <param2> parameter 2
        """
        )
    }

    @Test
    fun `reports missing action for first positional`() {
        class Nested : CliApp("cmd") {
            val action by action {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(emptyList(), formatter)

        hasUsageMessage(
            """
            Please specify an action to run.
            
            Usage: cmd <action>
            
            Available actions:
              one
              two run action two
        """
        )
    }

    @Test
    fun `reports missing action for subsequent positional`() {
        class Nested : CliApp("cmd") {
            val param by parameter("param")
            val action by action {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(listOf("arg"), formatter)

        hasUsageMessage(
            """
            Please specify an action to run.
            
            Usage: cmd <param> <action>
            
            Available actions:
              one
              two run action two
        """
        )
    }

    @Test
    fun `reports unknown action for first positional`() {
        class Nested : CliApp("cmd") {
            val action by action {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(listOf("unknown"), formatter)

        hasUsageMessage(
            """
            Unknown action: unknown
            
            Usage: cmd <action>
            
            Available actions:
              one
              two run action two
        """
        )
    }

    @Test
    fun `reports unknown action for subsequent positional`() {
        class Nested : CliApp("cmd") {
            val param by parameter("param")
            val action by action {
                action(Action(), "one")
                action(Action(), "two", help = "run action two")
            }
        }

        Nested().run(listOf("arg", "unknown"), formatter)

        hasUsageMessage(
            """
            Unknown action: unknown
            
            Usage: cmd <param> <action>
            
            Available actions:
              one
              two run action two
        """
        )
    }

    fun hasUsageMessage(message: String) {
        assertEquals(
            message.trimIndent() +
                    """

Run with --help for more information.
""", formatter.text
        )

    }
}