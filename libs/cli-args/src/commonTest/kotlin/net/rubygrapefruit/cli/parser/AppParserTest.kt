package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AppParserTest : AbstractParseTest() {
    class Action(val value: String?) : Runnable {
        override fun run() {
        }
    }

    @Test
    fun `parses arguments`() {
        val parser = app("some_app", params(option("x")) { Action(it) })

        assertIs<Action>(parser.parse("-x", "value").get()).apply {
            assertEquals("value", value)
        }
        assertIs<Action>(parser.parse().get()).apply {
            assertNull(value)
        }
    }

    @Test
    fun `reports app usage when app takes no parameters`() {
        val parser = app("some_app", params { DoNothing })

        assertEquals("some_app", parser.usage)
    }

    @Test
    fun `reports app usage when app takes option`() {
        val parser = app("some_app", params(option("x")) { Action(it) })

        assertEquals("some_app [-x <value>]", parser.usage)
    }

    @Test
    fun `reports app usage when app takes choice of option or command`() {
        val choices = oneOf(
            params(option("help").boolean().required()) { DoNothing },
            params(command("run")) { DoNothing }
        )
        val parser = app("some_app", choices)

        assertEquals(
            """some_app --help
some_app run""", parser.usage
        )
    }
}