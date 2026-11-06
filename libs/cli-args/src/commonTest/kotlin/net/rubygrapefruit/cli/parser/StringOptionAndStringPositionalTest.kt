package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringOptionAndStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses positional and option in any order`() {
        val parser = params(option("x"), positional("a")) { a, b -> Pair(a, b) }

        assertEquals(Pair("one", "two"), parser.parse("-x", "one", "two").get())
        assertEquals(Pair("one", "two"), parser.parse("two", "-x", "one").get())
        assertEquals(Pair(null, "two"), parser.parse("two").get())
    }

    @Test
    fun `must provide positional`() {
        val parser = params(option("x"), positional("a")) { _, _ -> fail() }

        parser.fails {
            failure(0, "Parameter <a> not provided")
        }
        parser.fails("-x", "value") {
            failure(2, "Parameter <a> not provided")
        }
    }

    @Test
    fun `option must have a value`() {
        val parser = params(option("x"), positional("a")) { _, _ -> fail() }

        parser.fails("value", "-x") {
            failure(2, "No value provided for option -x")
        }
        parser.fails("-x") {
            failure(1, "No value provided for option -x")
            failure(1, "Parameter <a> not provided")
        }
    }
}