package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ActionUsageTest {
    @Test
    fun `formats action with no configuration`() {
        class NoConfig : MainAction("cmd")

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --help Show usage message
            
        """.trimIndent(), NoConfig().usage().formatted
        )
    }

    @Test
    fun `formats action with arguments`() {
        class Arguments : MainAction("cmd") {
            val a1 by argument("a", help = "some argument")
            val a2 by argument("another-argument", help = "some other argument")
            val a3 by argument("no-help")
        }

        assertEquals(
            """
            Usage: cmd [options] <a> <another-argument> <no-help>
            
            Arguments:
              <a>                some argument
              <another-argument> some other argument

            Options:
              --help Show usage message
            
            """.trimIndent(), Arguments().usage().formatted
        )
    }

    @Test
    fun `formats action with options`() {
        class Options : MainAction("cmd") {
            val a1 by flag("thing", help = "some flag")
            val a2 by option("some-option", help = "some other option")
            val a3 by option("none")
        }

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --help                Show usage message
              --thing/--no-thing    some flag
              --some-option <value> some other option
              --none <value>

            """.trimIndent(), Options().usage().formatted
        )
    }
}