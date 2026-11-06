package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class RequiredBooleanOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x").boolean().required()

        assertEquals(Unit, parser.parse("-x").get())

        parser.fails("--x") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("-X") {
            failure(0, "Unexpected option '-X'. Try '-x' instead")
        }
    }

    @Test
    fun `parses option with long name`() {
        val parser = option("some-flag").boolean().required()

        assertEquals(Unit, parser.parse("--some-flag").get())

        parser.fails("-some-flag") {
            failure(0, "Unexpected option '-some-flag'. Try '--some-flag' instead")
        }
        parser.fails("--Some-Flag") {
            failure(0, "Unexpected option '--Some-Flag'. Try '--some-flag' instead")
        }
    }

    @Test
    fun `reports usage of option with short name`() {
        val parser = option("x").boolean().required()

        assertEquals("-x", parser.usage)
    }

    @Test
    fun `reports usage of option with long name`() {
        val parser = option("some-flag").boolean().required()

        assertEquals("--some-flag", parser.usage)
    }

    @Test
    fun `option must be present`() {
        val parser = option("some-flag").boolean().required()

        parser.fails {
            failure(0, "Option --some-flag not provided")
        }

        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Option --some-flag not provided")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'")
            failure(1, "Option --some-flag not provided")
        }
    }

    @Test
    fun `option must not have a value`() {
        val parser = option("some-flag").boolean().required()

        parser.fails("--some-flag", "value") {
            failure(1, "Unexpected argument 'value'")
        }
    }

    @Test
    fun `option cannot appear twice`() {
        val parser = option("x").boolean().required()

        parser.fails("-x", "-x") {
            failure(1, "Option -x can only appear once")
        }
        parser.fails("-x", "value", "-x") {
            failure(1, "Unexpected argument 'value'")
            failure(2, "Option -x can only appear once")
        }
    }
}