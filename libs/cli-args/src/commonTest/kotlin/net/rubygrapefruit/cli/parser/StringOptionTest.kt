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

        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
        }
        parser.fails("value") {
            failure(0, "Unexpected parameter 'value'")
        }
        parser.fails("-unknown", "value") {
            failure(0, "Unexpected option '-unknown'")
        }

        parser.fails("-X", "value") {
            failure(0, "Unexpected option '-X'")
        }
        parser.fails("--x", "value") {
            failure(0, "Unexpected option '--x'")
        }

        parser.fails("-x") {
            failure(1, "No value provided for option -x")
        }

        parser.fails("-x", "-y") {
            failure(1, "No value provided for option -x")
            failure(2, "Unexpected option '-y'")
        }

        parser.fails("-x", "value", "-y") {
            failure(2, "Unexpected option '-y'")
        }
        parser.fails("-x", "value", "value") {
            failure(2, "Unexpected parameter 'value'")
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
            failure(2, "Option -x can only appear once")
        }
    }
}