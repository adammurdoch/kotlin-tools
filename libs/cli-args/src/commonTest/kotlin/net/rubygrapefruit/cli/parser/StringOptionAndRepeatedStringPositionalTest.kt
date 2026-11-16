package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringOptionAndRepeatedStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses option and repeated positional in any order`() {
        val parser = params(option("x"), positional("a").repeated()) { x, a -> Pair(x, a) }

        assertEquals(Pair("value", listOf("one", "two")), parser.parse("-x", "value", "one", "two").get())
        assertEquals(Pair("value", listOf("one", "two")), parser.parse("one", "-x", "value", "two").get())
        assertEquals(Pair("value", listOf("one", "two")), parser.parse("one", "two", "-x", "value").get())

        assertEquals(Pair(null, listOf("one", "two")), parser.parse("one", "two").get())
        assertEquals(Pair(null, listOf("")), parser.parse("").get())
    }

    @Test
    fun `reports usage`() {
        val parser = params(option("x"), positional("a").repeated()) { _, _ -> fail() }

        parser.usage("[-x <value>] <a>...")
        parser.option("-x <value>")
        parser.positional("<a>")
    }
}