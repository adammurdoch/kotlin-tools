package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AppWithStringOptionTest: AbstractParseTest() {
    @Test
    fun `parses app with string option`() {
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
    fun `reports app usage`() {
        val parser = app("some_app", params(option("x")) { OneParam(it) })

        parser.usage("some_app [-x <value>]")
        parser.option("-x <value>")
    }
}