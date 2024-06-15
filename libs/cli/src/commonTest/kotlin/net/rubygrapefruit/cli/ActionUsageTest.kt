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
    fun `formats action with multiple parameters`() {
        class Parameters : MainAction("cmd") {
            val a1 by parameter("a", help = "some value")
            val a2 by parameter("another-param", help = "some other value")
            val a3 by parameter("no-help")
        }

        assertEquals(
            """
            Usage: cmd [options] <a> <another-param> <no-help>
            
            Parameters:
              <a>             some value
              <another-param> some other value

            Options:
              --help Show usage message
            
            """.trimIndent(), Parameters().usage().formatted
        )
    }

    @Test
    fun `formats action with parameters`() {
        class Parameters : MainAction("cmd") {
            val a1 by parameter("a", help = "some value")
            val a2 by parameters("param", help = "some other value")
        }

        assertEquals(
            """
            Usage: cmd [options] <a> <param>...
            
            Parameters:
              <a>     some value
              <param> some other value

            Options:
              --help Show usage message
            
            """.trimIndent(), Parameters().usage().formatted
        )
    }

    @Test
    fun `formats action with multiple options`() {
        class Options : MainAction("cmd") {
            val f1 by flag("thing", help = "some flag")
            val f2 by flag("t", help = "some short flag")
            val f3 by flag("f", "flag", help = "some other flag")
            val a2 by option("some-option", help = "some other option")
            val a3 by option("none")
            val a4 by option("o")
        }

        assertEquals(
            """
            Usage: cmd [options]
            
            Options:
              --help                Show usage message
              --thing, --no-thing   some flag
              -t                    some short flag
              -f, --flag, --no-flag some other flag
              --some-option <value> some other option
              --none <value>
              -o <value>

            """.trimIndent(), Options().usage().formatted
        )
    }
}