package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MultipleMultipleOptionsTest : AbstractParseTest() {
    @Test
    fun `parses multiple sets of string options in any order`() {
        val parser = params(
            params(option("a"), option("b")) { a, b -> listOf(a, b) },
            params(option("c"), option("d")) { c, d -> listOf(c, d) }
        ) { first, second -> first + second }

        assertEquals(listOf("one", "two", "three", "four"), parser.parse("-a", "one", "-b", "two", "-c", "three", "-d", "four").get())
        assertEquals(listOf("one", "two", "three", "four"), parser.parse("-c", "three", "-d", "four", "-a", "one", "-b", "two").get())

        assertEquals(listOf("one", "two", null, null), parser.parse("-b", "two", "-a", "one").get())
        assertEquals(listOf(null, null, "three", "four"), parser.parse("-c", "three", "-d", "four").get())

        assertEquals(listOf("one", null, null, null), parser.parse("-a", "one").get())
        assertEquals(listOf(null, "two", null, null), parser.parse("-b", "two").get())
        assertEquals(listOf(null, null, "three", null), parser.parse("-c", "three").get())
        assertEquals(listOf(null, null, null, "four"), parser.parse("-d", "four").get())

        assertEquals(listOf(null, null, null, null), parser.parse().get())
    }

    @Test
    fun `parses multiple sets of overlapping string options`() {
        val parser = params(
            params(option("a"), option("b")) { a, b -> listOf(a, b) },
            params(option("a"), option("b")) { a, b -> listOf(a, b) }
        ) { first, second -> first + second }

        assertEquals(listOf("one", "two", "three", "four"), parser.parse("-a", "one", "-b", "two", "-a", "three", "-b", "four").get())
        assertEquals(listOf("one", "two", "three", "four"), parser.parse("-a", "one", "-a", "three", "-b", "two", "-b", "four").get())
        assertEquals(listOf("one", "two", "three", "four"), parser.parse("-b", "two", "-b", "four", "-a", "one", "-a", "three").get())

        assertEquals(listOf("one", "two", null, null), parser.parse("-b", "two", "-a", "one").get())
        assertEquals(listOf("one", "two", null, "four"), parser.parse("-b", "two", "-b", "four", "-a", "one").get())

        assertEquals(listOf(null, null, null, null), parser.parse().get())
    }

    @Test
    fun `reports usage`() {
        val parser = params(
            params(option("a"), option("b")) { _, _ -> fail() },
            params(option("c"), option("d")) { _, _ -> fail() }
        ) { _, _ -> fail() }

        parser.usage("[-a <value>] [-b <value>] [-c <value>] [-d <value>]")
        parser.options {
            option("-a <value>")
            option("-b <value>")
            option("-c <value>")
            option("-d <value>")
        }
        parser.noPositionals()
    }
}