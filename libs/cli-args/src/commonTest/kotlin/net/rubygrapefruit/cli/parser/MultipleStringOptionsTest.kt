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

    @Test
    fun `cannot include other input values`() {
        val parser = allOf(option("a"), option("b")) { a, b -> Pair(a, b) }

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
    fun `option must have a value`() {
        val parser = allOf(option("a"), option("b")) { a, b -> Pair(a, b) }

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