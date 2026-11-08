package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class AppParserTest : AbstractParseTest() {
    @Test
    fun `parses arguments`() {
        val parser = app("some_app", option("x"))

        assertEquals("value", parser.parse("-x", "value").get())
        assertEquals(null, parser.parse().get())
    }

    @Test
    fun `reports app usage when app takes no parameters`() {
        val parser = app("some_app", params { "run" })

        assertEquals("some_app", parser.usage)
    }

    @Test
    fun `reports app usage when app takes option`() {
        val parser = app("some_app", option("x"))

        assertEquals("some_app [-x <value>]", parser.usage)
    }

    @Test
    fun `reports app usage when app takes choice of option or command`() {
        val choices = oneOf(
            option("help").boolean().required(),
            command("run")
        )
        val parser = app("some_app", choices)

        assertEquals(
            """some_app --help
some_app run""", parser.usage
        )
    }
}