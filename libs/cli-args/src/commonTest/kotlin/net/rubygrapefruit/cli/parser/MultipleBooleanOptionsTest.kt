package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleBooleanOptionsTest : AbstractParseTest() {
    @Test
    fun `parses two options with short name in any order`() {
        val parser = params(
            option("a").boolean(),
            option("b").boolean()
        ) { a, b -> listOf(a, b) }

        assertEquals(listOf(true, true), parser.parse("-a", "-b").get())
        assertEquals(listOf(true, true), parser.parse("-b", "-a").get())

        assertEquals(listOf(true, false), parser.parse("-a").get())
        assertEquals(listOf(false, true), parser.parse("-b").get())

        assertEquals(listOf(false, false), parser.parse().get())
    }

    @Test
    fun `parses two options with long name in any order`() {
        val parser = params(
            option("option-a").boolean(),
            option("option-b").boolean()
        ) { a, b -> listOf(a, b) }

        assertEquals(listOf(true, true), parser.parse("--option-a", "--option-b").get())
        assertEquals(listOf(true, true), parser.parse("--option-b", "--option-a").get())

        assertEquals(listOf(true, false), parser.parse("--option-a").get())
        assertEquals(listOf(false, true), parser.parse("--option-b").get())

        assertEquals(listOf(false, false), parser.parse().get())
    }

    @Test
    fun `parses three options in any order`() {
        val parser = params(
            option("a").boolean(),
            option("b").boolean(),
            option("long").boolean()
        ) { a, b, c -> listOf(a, b, c) }

        assertEquals(listOf(true, true, true), parser.parse("-a", "-b", "--long").get())
        assertEquals(listOf(true, true, true), parser.parse("--long", "-b", "-a").get())

        assertEquals(listOf(true, false, false), parser.parse("-a").get())
        assertEquals(listOf(false, true, false), parser.parse("-b").get())
        assertEquals(listOf(false, false, true), parser.parse("--long").get())

        assertEquals(listOf(false, false, false), parser.parse().get())
    }
}