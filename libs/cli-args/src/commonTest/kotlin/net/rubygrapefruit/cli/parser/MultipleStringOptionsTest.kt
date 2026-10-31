package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleStringOptionsTest : AbstractParseTest() {
    @Test
    fun `parses two options in any order`() {
        val parser = allOf(option("a"), option("b")) { a, b -> Pair(a, b) }

        assertEquals(Pair("one", "two"), parser.parse("-a", "one", "-b", "two").get())
        assertEquals(Pair("one", "two"), parser.parse("-b", "two", "-a", "one").get())

        assertEquals(Pair("one", null), parser.parse("-a", "one").get())
        assertEquals(Pair(null, "two"), parser.parse("-b", "two").get())

        assertEquals(Pair(null, null), parser.parse().get())
    }
}