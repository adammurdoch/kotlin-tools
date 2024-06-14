package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ActionUsageTest {
    @Test
    fun `formats command with no configuration`() {
        class NoConfig : Action()

        assertEquals(
            """
            Usage: <cmd>
            
        """.trimIndent(), NoConfig().usage().formatted
        )
    }

    @Test
    fun `formats command with arguments`() {
        class Arguments : Action() {
            val a1 by argument("a", help = "some argument")
            val a2 by argument("another-argument", help = "some other argument")
            val a3 by argument("no-help")
        }

        assertEquals(
            """
            Usage: <cmd> <a> <another-argument> <no-help>
            
            Arguments:
              a                some argument
              another-argument some other argument

            """.trimIndent(), Arguments().usage().formatted
        )
    }

    @Test
    fun `formats command with options`() {
        class Options : Action() {
            val a1 by flag("thing", help = "some flag")
            val a2 by option("some-option", help = "some other option")
            val a3 by option("no-help")
        }

        assertEquals(
            """
            Usage: <cmd> [options]
            
            Options:
              --thing/--no-thing    some flag
              --some-option <value> some other option
              --no-help <value>

            """.trimIndent(), Options().usage().formatted
        )
    }
}