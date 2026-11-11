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

        parser.usage("some_app")
        parser.noOptions()
    }

    @Test
    fun `reports app usage when app takes option`() {
        val parser = app("some_app", params(option("x")) { OneParam(it) })

        parser.usage("some_app [-x <value>]")
        parser.option("-x <value>")
    }

    @Test
    fun `reports app usage when app takes choice of option or command`() {
        val choices = oneOf(
            params(option("help").boolean().required()) { NoParams() },
            params(command("run")) { NoParams() }
        )
        val parser = app("some_app", choices)

        parser.usage {
            choice("some_app --help")
            choice("some_app run")
        }
        parser.options {
            option("--help")
        }
    }
}