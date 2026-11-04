package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringOptionAndParameterTest : AbstractParseTest() {
    @Test
    fun `parses parameter and option in any order`() {
        val parser = allOf(option("x"), parameter("a")) { a, b -> Pair(a, b) }

        assertEquals(Pair("one", "two"), parser.parse("-x", "one", "two").get())
        assertEquals(Pair("one", "two"), parser.parse("two", "-x", "one").get())
        assertEquals(Pair(null, "two"), parser.parse("two").get())
    }

    @Test
    fun `must provide parameter`() {
        val parser = allOf(option("x"), parameter("a")) { a, b -> fail() }

        parser.fails {
            failure(0, "Parameter <a> not provided")
        }
        parser.fails("-x", "value") {
            failure(2, "Parameter <a> not provided")
        }
    }

    @Test
    fun `option must have a value`() {
        val parser = allOf(option("x"), parameter("a")) { a, b -> fail() }

        parser.fails("value", "-x") {
            failure(2, "No value provided for option -x")
        }
        parser.fails("-x") {
            failure(1, "No value provided for option -x")
            failure(1, "Parameter <a> not provided")
        }
    }
}