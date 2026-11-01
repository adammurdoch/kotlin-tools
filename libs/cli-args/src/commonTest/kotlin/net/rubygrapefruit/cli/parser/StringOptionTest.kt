package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x")

        assertEquals("value", parser.parse("-x", "value").get())
        assertEquals("", parser.parse("-x", "").get())
        assertNull(parser.parse().get())

        parser.fails("-X", "value") {
            failure(0, "Unexpected option '-X'")
            failure(1, "Unexpected argument 'value'")
        }
        parser.fails("--x", "value") {
            failure(0, "Unexpected option '--x'")
            failure(1, "Unexpected argument 'value'")
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = option("x")

        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
        }
        parser.fails("value") {
            failure(0, "Unexpected argument 'value'")
        }
        parser.fails("-unknown", "value") {
            failure(0, "Unexpected option '-unknown'")
            failure(1, "Unexpected argument 'value'")
        }
        parser.fails("value", "--unknown") {
            failure(0, "Unexpected argument 'value'")
            failure(1, "Unexpected option '--unknown'")
        }

        parser.fails("-x", "value", "-unknown") {
            failure(2, "Unexpected option '-unknown'")
        }
        parser.fails("-x", "value", "value") {
            failure(2, "Unexpected argument 'value'")
        }
    }

    @Test
    fun `option must have value`() {
        val parser = option("x")

        parser.fails("-x") {
            failure(1, "No value provided for option -x")
        }

        parser.fails("-x", "-unknown") {
            failure(1, "No value provided for option -x")
            failure(1, "Unexpected option '-unknown'")
        }

        parser.fails("-unknown", "-x") {
            failure(0, "Unexpected option '-unknown'")
            failure(2, "No value provided for option -x")
        }
    }

    @Test
    fun `option cannot appear twice`() {
        val parser = option("x")

        parser.fails("-x", "value", "-x", "value") {
            failure(2, "Option -x can only appear once")
        }
        parser.fails("-x", "value", "-x") {
            failure(2, "Option -x can only appear once")
        }
        parser.fails("-x", "-x", "value") {
            failure(1, "No value provided for option -x")
            failure(1, "Option -x can only appear once")
        }
    }
}