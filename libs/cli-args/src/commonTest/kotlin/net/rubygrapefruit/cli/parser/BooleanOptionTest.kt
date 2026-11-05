package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x").boolean()

        assertEquals(true, parser.parse("-x").get())
        assertEquals(false, parser.parse().get())

        parser.fails("--x") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("-X") {
            failure(0, "Unexpected option '-X'")
        }
    }

    @Test
    fun `parses option with long name`() {
        val parser = option("some-flag").boolean()

        assertEquals(true, parser.parse("--some-flag").get())
        assertEquals(false, parser.parse().get())

        parser.fails("-some-flag") {
            failure(0, "Unexpected option '-some-flag'. Try '--some-flag' instead")
        }
    }

    @Test
    fun `option must not have a value`() {
        val parser = option("x").boolean()

        parser.fails("-x", "value") {
            failure(1, "Unexpected argument 'value'")
        }
    }

    @Test
    fun `option cannot appear twice`() {
        val parser = option("x").boolean()

        parser.fails("-x", "-x") {
            failure(1, "Option -x can only appear once")
        }
        parser.fails("-x", "value", "-x") {
            failure(1, "Unexpected argument 'value'")
            failure(2, "Option -x can only appear once")
        }
        parser.fails("-x", "-x", "value") {
            failure(1, "Option -x can only appear once")
            failure(2, "Unexpected argument 'value'")
        }
    }
}