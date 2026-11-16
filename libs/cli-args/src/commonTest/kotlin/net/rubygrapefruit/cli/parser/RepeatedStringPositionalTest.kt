package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class RepeatedStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses one or more positional parameters`() {
        val parser = positional("a").repeated()

        assertEquals(listOf("value 1"), parser.parse("value 1").get())
        assertEquals(listOf("value 1", "value 2"), parser.parse("value 1", "value 2").get())
        assertEquals(listOf("value 1", "", "value 2"), parser.parse("value 1", "", "value 2").get())
    }

    @Test
    fun `reports usage`() {
        val parser = positional("a").repeated()

        parser.usage("<a>...")
        parser.noOptions()
        parser.positional("<a>")
    }

    @Test
    fun `can provide summary`() {
        val parser = positional("a", summary = "Something useful").repeated()
        parser.usage("<a>...")
        parser.positional("<a>", "Something useful")
    }

    @Test
    fun `positional must be provided`() {
        val parser = positional("a").repeated()

        parser.fails {
            failure(0, "Parameter <a> not provided")
        }
    }
}