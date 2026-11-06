package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MultipleStringOptionsTest : AbstractParseTest() {
    @Test
    fun `parses two options with short name in any order`() {
        val parser = params(option("a"), option("b")) { a, b -> listOf(a, b) }

        assertEquals(listOf("one", "two"), parser.parse("-a", "one", "-b", "two").get())
        assertEquals(listOf("one", "two"), parser.parse("-b", "two", "-a", "one").get())

        assertEquals(listOf("one", null), parser.parse("-a", "one").get())
        assertEquals(listOf(null, "two"), parser.parse("-b", "two").get())

        assertEquals(listOf(null, null), parser.parse().get())

        parser.fails("--a", "value", "-b", "value") {
            failure(0, "Unexpected option '--a'. Try '-a' instead")
        }
        parser.fails("-a", "value", "--b", "value") {
            failure(2, "Unexpected option '--b'. Try '-b' instead")
        }
    }

    @Test
    fun `parses two options with long name in any order`() {
        val parser = params(option("option-a"), option("option-b")) { a, b -> listOf(a, b) }

        assertEquals(listOf("one", "two"), parser.parse("--option-a", "one", "--option-b", "two").get())
        assertEquals(listOf("one", "two"), parser.parse("--option-b", "two", "--option-a", "one").get())

        assertEquals(listOf("one", null), parser.parse("--option-a", "one").get())
        assertEquals(listOf(null, "two"), parser.parse("--option-b", "two").get())

        assertEquals(listOf(null, null), parser.parse().get())

        parser.fails("-option-a", "value", "--option-b", "value") {
            failure(0, "Unexpected option '-option-a'. Try '--option-a' instead")
        }
        parser.fails("--option-a", "value", "-option-b", "value") {
            failure(2, "Unexpected option '-option-b'. Try '--option-b' instead")
        }
    }

    @Test
    fun `parses three options in any order`() {
        val parser = params(option("a"), option("b"), option("long")) { a, b, c -> listOf(a, b, c) }

        assertEquals(listOf("1", "2", "3"), parser.parse("-a", "1", "-b", "2", "--long", "3").get())
        assertEquals(listOf("1", "2", "3"), parser.parse("--long", "3", "-a", "1", "-b", "2").get())

        assertEquals(listOf("1", null, null), parser.parse("-a", "1").get())
        assertEquals(listOf(null, "2", null), parser.parse("-b", "2").get())
        assertEquals(listOf(null, null, "3"), parser.parse("--long", "3").get())

        assertEquals(listOf(null, null, null), parser.parse().get())
    }

    @Test
    fun `reports usage for two options with short name`() {
        val parser = params(option("a"), option("b")) { a, b -> listOf(a, b) }

        assertEquals("[-a <value>] [-b <value>]", parser.usage)
    }

    @Test
    fun `reports usage for three options`() {
        val parser = params(option("a"), option("b"), option("long")) { a, b, c -> listOf(a, b, c) }

        assertEquals("[-a <value>] [-b <value>] [--long <value>]", parser.usage)
    }

    @Test
    fun `cannot include other input values`() {
        val parser = params(option("a"), option("b")) { _, _ -> fail() }

        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("value") {
            failure(0, "Unexpected argument 'value'")
        }

        parser.fails("-a", "one", "-unknown") {
            failure(2, "Unexpected option '-unknown'")
        }
        parser.fails("-b", "two", "-unknown") {
            failure(2, "Unexpected option '-unknown'")
        }
    }

    @Test
    fun `each option must have a value`() {
        val parser = params(option("a"), option("b")) { _, _ -> fail() }

        parser.fails("-a", "-b") {
            failure(1, "No value provided for option -a")
            failure(2, "No value provided for option -b")
        }

        parser.fails("-a", "-unknown", "-b", "--unknown") {
            failure(1, "No value provided for option -a")
            failure(1, "Unexpected option '-unknown'")
            failure(3, "No value provided for option -b")
            failure(3, "Unexpected option '--unknown'")
        }
    }
}