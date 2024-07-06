package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertEquals

class HelpActionTest : AbstractActionTest() {
    private val formatter = BufferingFormatter()

    @Test
    fun `generates help output for app with no configuration`() {
        val help = HelpAction("cmd", Action(), formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd

        """.trimIndent(), formatter.text
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
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd <z> <another-param> <no-help>
            
            Parameters:
              <another-param> some other value
              <z>             some value
            
            """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `generates help output for app with multiple flags`() {
        class App : Action() {
            val f1 by flag("some-flag", help = "some other flag")
            val f2 by flag("f", "flag", help = "a flag")
            val f3 by flag("none")
            val f4 by flag("o", help = "single character")
        }

        val app = App()
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --none, --no-none
              --some-flag, --no-some-flag some other flag
              -f, --flag, --no-flag       a flag
              -o                          single character
            
            """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `generates help output for app with choice options`() {
        class App : Action() {
            val o1 by oneOf {
                choice(1, "one", help = "select 1")
                choice(2, "2", help = "select 2")
                choice(3, "three", "3", help = "select 3")
                choice(4, "none")
            }.flags()
        }

        val app = App()
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --none
              --one       select 1
              --three, -3 select 3
              -2          select 2
            
            """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `generates help output for app with multiple options`() {
        class App : Action() {
            val a1 by option("some-option", help = "some other option")
            val a2 by option("s", "second-option", help = "second option")
            val a3 by option("none")
            val a4 by option("o", help = "single character")
        }

        val app = App()
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --none <value>
              --some-option <value>               some other option
              -o <value>                          single character
              -s <value>, --second-option <value> second option
            
            """.trimIndent(), formatter.text
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
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd <action>
            
            Actions:
              a2         run action a2
              action-two
              z          run action z
            
            """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `generates help output for app with nested actions referenced by name and default action with configuration`() {
        class Sub : Action() {
            val param by parameter("param", help = "some parameter")
            val option by option("option", help = "some option")
        }

        class App : Action() {
            val action by actions {
                action(Action(), "z", help = "run action z")
                action(Action(), "action-two")
                action(Action(), "a2", help = "run action a2")
                action(Sub(), help = "run main action")
            }
        }

        val app = App()
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd [options] <action>
            
            Actions:
              <param>    run main action
              a2         run action a2
              action-two
              z          run action z
            
            Options:
              --option <value> some option

            """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `generates help output for app with nested actions referenced by name and default action with nested actions`() {
        class Sub : Action() {
            val action by actions {
                action(Action(), "z2", help = "run action z2")
                action(Action(), "a", help = "run action a")
            }
        }

        class App : Action() {
            val action by actions {
                action(Action(), "z", help = "run action z")
                action(Action(), "action-two")
                action(Action(), "a2", help = "run action a2")
                action(Sub(), help = "run main action")
            }
        }

        val app = App()
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd <action>
            
            Actions:
              a          run action a
              a2         run action a2
              action-two
              z          run action z
              z2         run action z2

            """.trimIndent(), formatter.text
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
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --a2         run action a2
              --action-two
              -z           run action z
            
            """.trimIndent(), formatter.text
        )
    }

    @Test
    fun `generates help output for app with nested actions referenced as options and default action with configuration`() {
        class Sub : Action() {
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
        val help = HelpAction("cmd", app, formatter)
        help.run()

        assertEquals(
            """
            Usage: cmd [options] <param>
            
            Parameters:
              <param> some parameter

            Options:
              --a2             run action a2
              --option <value> some option
              -z               run action z
            
            """.trimIndent(), formatter.text
        )
    }
}