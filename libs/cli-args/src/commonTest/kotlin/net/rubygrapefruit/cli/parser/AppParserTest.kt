package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AppParserTest : AbstractParseTest() {
    @Test
    fun `parses arguments`() {
        val parser = app("some_app", params(option("x")) { OneParam(it) })

        parser.parse("-x", "value").get().apply {
            assertIs<OneParam>(this)
            assertEquals("value", value)
        }
        parser.parse().get().apply {
            assertIs<OneParam>(this)
            assertNull(value)
        }
    }

    @Test
    fun `reports app usage when app takes no parameters`() {
        val parser = app("some_app", params { NoParams() })

        assertEquals("some_app", parser.usage)
    }

    @Test
    fun `reports app usage when app takes option`() {
        val parser = app("some_app", params(option("x")) { OneParam(it) })

        assertEquals("some_app [-x <value>]", parser.usage)
    }

    @Test
    fun `reports app usage when app takes choice of option or command`() {
        val choices = oneOf(
            params(option("help").boolean().required()) { NoParams() },
            params(command("run")) { NoParams() }
        )
        val parser = app("some_app", choices)

        assertEquals(
            """some_app --help
some_app run""", parser.usage
        )
    }
}