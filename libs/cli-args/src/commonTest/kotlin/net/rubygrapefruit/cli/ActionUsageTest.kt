package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ActionUsageTest {
    @Test
    fun `formats action with no configuration`() {
        class NoConfig : TestApp()

        assertEquals(
            """
            [options]
            
            Options:
              --help, --no-help help message
            
        """.trimIndent(), NoConfig().usage().formatted
        )
    }

    @Test
    fun `formats action with multiple parameters`() {
        class Parameters : TestApp() {
            val p1 by parameter("z", help = "some value")
            val p2 by parameter("another-param", help = "some other value")
            val p3 by parameter("no-help")
        }

        assertEquals(
            """
            [options] <z> <another-param> <no-help>
            
            Parameters:
              <another-param> some other value
              <z>             some value

            Options:
              --help, --no-help help message
            
            """.trimIndent(), Parameters().usage().formatted
        )
    }

    @Test
    fun `formats action with multi-value parameter`() {
        class Parameters : TestApp() {
            val a1 by parameter("a", help = "some value")
            val a2 by parameters("param", help = "some other value")
        }

        assertEquals(
            """
            [options] <a> <param>...
            
            Parameters:
              <a>     some value
              <param> some other value

            Options:
              --help, --no-help help message
            
            """.trimIndent(), Parameters().usage().formatted
        )
    }

    @Test
    fun `formats action with multiple flags`() {
        class Options : TestApp() {
            val f1 by flag("thing", help = "some flag")
            val f2 by flag("t", help = "some short flag")
            val f3 by flag("f", "flag", help = "some other flag")
        }

        assertEquals(
            """
            [options]
            
            Options:
              --help, --no-help     help message
              --thing, --no-thing   some flag
              -f, --flag, --no-flag some other flag
              -t                    some short flag

            """.trimIndent(), Options().usage().formatted
        )
    }

    @Test
    fun `formats action with multiple options`() {
        class Options : TestApp() {
            val a1 by option("some-option", help = "some other option")
            val a2 by option("s", "second-option", help = "second option")
            val a3 by option("none")
            val a4 by option("o", help = "single character")
        }

        assertEquals(
            """
            [options]
            
            Options:
              --help, --no-help                   help message
              --none <value>
              --some-option <value>               some other option
              -o <value>                          single character
              -s <value>, --second-option <value> second option

            """.trimIndent(), Options().usage().formatted
        )
    }

    @Test
    fun `formats action with multiple choices`() {
        class Options : TestApp() {
            val c1 by oneOf {
                choice(1, "1", help = "select 1")
                choice(2, "two")
                choice(12, "12", help = "select 12")
            }.flags()
            val c2 by oneOf {
                choice("a", "a", help = "select a")
                choice("b", "long")
                choice("c", "other", help = "select c")
            }.flags()
        }

        assertEquals(
            """
            [options]
            
            Options:
              --12              select 12
              --help, --no-help help message
              --long
              --other           select c
              --two
              -1                select 1
              -a                select a

            """.trimIndent(), Options().usage().formatted
        )
    }

    @Test
    fun `formats action with multiple actions`() {
        class Options : TestApp() {
            val a1 by actions {
                action(Action(), "z", help = "run action z")
                action(Action(), "action-two")
                action(Action(), "a2", help = "run action a2")
            }
            val a2 by actions {
                action(Action(), "z2", help = "run action z2")
                action(Action(), "action-three")
            }
        }

        assertEquals(
            """
            [options] <action> <action>

            Actions:
              a2           run action a2
              action-three
              action-two
              z            run action z
              z2           run action z2

            Options:
              --help, --no-help help message

            """.trimIndent(), Options().usage().formatted
        )
    }
}