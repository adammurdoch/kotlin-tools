package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class StringOptionAndParameterTest : AbstractParseTest() {
    @Test
    fun `parses parameter and option in any order`() {
        val parser = allOf(option("x"), parameter("a")) { a, b -> Pair(a, b) }

        assertEquals(Pair("one", "two"), parser.parse("-x", "one", "two").get())
        assertEquals(Pair("one", "two"), parser.parse("two", "-x", "one").get())
        assertEquals(Pair(null, "two"), parser.parse("two").get())
    }
}