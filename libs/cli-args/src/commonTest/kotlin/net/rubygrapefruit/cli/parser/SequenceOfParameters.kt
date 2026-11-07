package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class SequenceOfParameters : AbstractParseTest() {
    @Test
    fun `parses command and options then command and options`() {
        val command1 = params(command("one"), option("a")) { _, a -> a }
        val command2 = params(command("two"), option("b")) { _, a -> a }
        val parser = params(command1, command2) { a, b -> listOf(a, b) }

        assertEquals(listOf("1", "2"), parser.parse("one", "-a", "1", "two", "-b", "2").get())
        assertEquals(listOf(null, "2"), parser.parse("one", "two", "-b", "2").get())
        assertEquals(listOf("1", null), parser.parse("one", "-a", "1", "two").get())
        assertEquals(listOf(null, null), parser.parse("one", "two").get())
    }

    @Test
    fun `reports usage of command and options then command and options`() {
        val command1 = params(command("one"), option("a")) { _, a -> a }
        val command2 = params(command("two"), option("b")) { _, a -> a }
        val parser = params(command1, command2) { a, b -> listOf(a, b) }

        assertEquals("one [-a <value>] two [-b <value>]", parser.usage)
    }
}