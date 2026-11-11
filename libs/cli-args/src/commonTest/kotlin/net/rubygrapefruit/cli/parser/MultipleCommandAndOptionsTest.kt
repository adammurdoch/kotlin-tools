package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MultipleCommandAndOptionsTest : AbstractParseTest() {
    @Test
    fun `parses command and options then command and options`() {
        val command1 = params(command("one"), option("a")) { it }
        val command2 = params(command("two"), option("b")) { it }
        val parser = params(command1, command2) { a, b -> listOf(a, b) }

        assertEquals(listOf("1", "2"), parser.parse("one", "-a", "1", "two", "-b", "2").get())
        assertEquals(listOf("1", "2"), parser.parse("-a", "1", "one", "two", "-b", "2").get())
        assertEquals(listOf("1", "2"), parser.parse("-b", "2", "-a", "1", "one", "two").get())
        assertEquals(listOf(null, null), parser.parse("one", "two").get())
    }

    @Test
    fun `reports usage`() {
        val command1 = params(command("one"), option("a")) { it }
        val command2 = params(command("two"), option("b")) { it }
        val parser = params(command1, command2) { _, _ -> fail() }

        assertEquals("one [-a <value>] two [-b <value>]", parser.usage)

        val options = parser.options
        assertEquals(2, options.size)
        assertEquals("-a <value>", options[0].optionUsage)
        assertEquals("-b <value>", options[1].optionUsage)
    }

    @Test
    fun `commands must be provided in specified order`() {
        val command1 = params(command("one"), option("a")) { it }
        val command2 = params(command("two"), option("b")) { it }
        val parser = params(command1, command2) { a, b -> listOf(a, b) }

//        parser.fails("two", "one") {
//            failure(0, "??")
//        }
        parser.fails("one") {
            failure(1, "Expected 'two'")
        }
        parser.fails("two") {
//            failure(0, "Expected 'one'")
            failure(1, "Expected 'one'")
        }
    }
}