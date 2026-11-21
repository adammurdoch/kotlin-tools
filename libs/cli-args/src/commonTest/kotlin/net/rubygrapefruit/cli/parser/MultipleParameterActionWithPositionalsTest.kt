package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleParameterActionWithPositionalsTest : AbstractActionParseTest() {
    @Test
    fun `parses two positionals`() {
        var value: List<String>? = null
        val parser = action(
            positional("a"),
            positional("b")
        ) { a, b -> value = listOf(a, b) }

        parser.parse("one", "two").get().run {
            TestContext.run()
        }
        assertEquals(listOf("one", "two"), value)
    }

    @Test
    fun `parses three positionals`() {
        var value: List<String>? = null
        val parser = action(
            positional("a"),
            positional("b"),
            positional("c")
        ) { a, b, c -> value = listOf(a, b, c) }

        parser.parse("one", "two", "three").get().run {
            TestContext.run()
        }
        assertEquals(listOf("one", "two", "three"), value)
    }

    @Test
    fun `parses four positionals`() {
        var value: List<String>? = null
        val parser = action(
            positional("a"),
            positional("b"),
            positional("c"),
            positional("d")
        ) { a, b, c, d -> value = listOf(a, b, c, d) }

        parser.parse("one", "two", "three", "four").get().run {
            TestContext.run()
        }
        assertEquals(listOf("one", "two", "three", "four"), value)
    }

    @Test
    fun `parses five positionals`() {
        var value: List<String>? = null
        val parser = action(
            positional("a"),
            positional("b"),
            positional("c"),
            positional("d"),
            positional("e")
        ) { a, b, c, d, e -> value = listOf(a, b, c, d, e) }

        parser.parse("one", "two", "three", "four", "five").get().run {
            TestContext.run()
        }
        assertEquals(listOf("one", "two", "three", "four", "five"), value)
    }
}