package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MultipleStringParametersTest : AbstractParseTest() {
    @Test
    fun `parses two parameters in order`() {
        val parser = allOf(parameter("a"), parameter("b")) { a, b -> Pair(a, b) }

        assertEquals(Pair("one", "two"), parser.parse("one", "two").get())
    }

    @Test
    fun `parameters must be provided`() {
        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> fail() }

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
        val parser = allOf(parameter("a"), parameter("b")) { _, _ -> fail() }

        parser.fails("one", "two", "other") {
            failure(2, "Unexpected argument 'other'")
        }
        parser.fails("one", "--unknown", "two") {
            failure(1, "Unexpected option '--unknown'")
        }
    }
}