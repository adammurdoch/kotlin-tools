package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MultipleStringPositionalsTest : AbstractParseTest() {
    @Test
    fun `parses two parameters in order`() {
        val parser = params(positional("a"), positional("b")) { a, b -> listOf(a, b) }

        assertEquals(listOf("one", "two"), parser.parse("one", "two").get())
    }

    @Test
    fun `parses three parameters in order`() {
        val parser = params(positional("a"), positional("b"), positional("c")) { a, b, c -> listOf(a, b, c) }

        assertEquals(listOf("one", "two", "three"), parser.parse("one", "two", "three").get())
    }

    @Test
    fun `parameters must be provided`() {
        val parser = params(positional("a"), positional("b")) { _, _ -> fail() }

        parser.fails {
            failure(0, "Parameter <a> not provided")
            failure(0, "Parameter <b> not provided")
        }
        parser.fails("one") {
            failure(1, "Parameter <b> not provided")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Parameter <a> not provided")
            failure(1, "Parameter <b> not provided")
        }
        parser.fails("one", "--unknown") {
            failure(1, "Unexpected option '--unknown'")
            failure(2, "Parameter <b> not provided")
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = params(positional("a"), positional("b")) { _, _ -> fail() }

        parser.fails("one", "two", "other") {
            failure(2, "Unexpected argument 'other'")
        }
        parser.fails("one", "--unknown", "two") {
            failure(1, "Unexpected option '--unknown'")
        }
        parser.fails("one", "-unknown", "two") {
            failure(1, "Unexpected option '-unknown'")
        }
    }
}