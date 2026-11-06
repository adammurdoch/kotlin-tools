package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class OneStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = params(option("a")) { a -> a?.length }

        assertEquals(3, parser.parse("-a", "one").get())
        assertEquals(null, parser.parse().get())

        parser.fails("--a", "value") {
            failure(0, "Unexpected option '--a'. Try '-a' instead")
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = params(option("a")) { _ -> fail() }

        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("value") {
            failure(0, "Unexpected argument 'value'")
        }

        parser.fails("-a", "one", "-unknown") {
            failure(2, "Unexpected option '-unknown'")
        }
        parser.fails("value", "-a", "one") {
            failure(0, "Unexpected argument 'value'")
        }
    }
}