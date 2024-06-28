package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertEquals

class HelpActionTest : AbstractActionTest() {
    @Test
    fun `generates help output for app with no configuration`() {
        val help = HelpAction("cmd", Action())

        assertEquals(
            """
            Usage: cmd

        """.trimIndent(), help.formatted
        )
    }

    @Test
    fun `generates help output for app with multiple parameters`() {
        class App : Action() {
            val p1 by parameter("z", help = "some value")
            val p2 by parameter("another-param", help = "some other value")
            val p3 by parameter("no-help")
        }

        val app = App()
        val help = HelpAction("cmd", app)

        assertEquals(
            """
            Usage: cmd <z> <another-param> <no-help>
            
            Parameters:
              <another-param> some other value
              <z>             some value
            
            """.trimIndent(), help.formatted
        )
    }

    @Test
    fun `generates help output for app with nested actions referenced by name`() {
        class App : Action() {
            val action by actions {
                action(Action(), "z", help = "run action z")
                action(Action(), "action-two")
                action(Action(), "a2", help = "run action a2")
            }
        }

        val app = App()
        val help = HelpAction("cmd", app)

        assertEquals(
            """
            Usage: cmd <action>
            
            Actions:
              a2         run action a2
              action-two
              z          run action z
            
            """.trimIndent(), help.formatted
        )
    }

    @Test
    fun `generates help output for app with nested actions referenced as options`() {
        class App : Action() {
            val action by actions {
                option(Action(), "z", help = "run action z")
                option(Action(), "action-two")
                option(Action(), "a2", help = "run action a2")
            }
        }

        val app = App()
        val help = HelpAction("cmd", app)

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --a2         run action a2
              --action-two
              -z           run action z
            
            """.trimIndent(), help.formatted
        )
    }

    @Test
    fun `generates help output for app with nested actions as options and default action with configuration`() {
        class Sub: Action() {
            val param by parameter("param", help = "some parameter")
            val option by option("option", help = "some option")
        }
        class App : Action() {
            val action by actions {
                option(Action(), "z", help = "run action z")
                action(Sub(), help = "run main action")
                option(Action(), "a2", help = "run action a2")
            }
        }

        val app = App()
        val help = HelpAction("cmd", app)

        assertEquals(
            """
            Usage: cmd [options] <param>
            
            Parameters:
              <param> some parameter

            Options:
              --a2             run action a2
              --option <value> some option
              -z               run action z
            
            """.trimIndent(), help.formatted
        )
    }
}