package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x").boolean()

        assertEquals(true, parser.parse("-x").get())
        assertEquals(false, parser.parse().get())
    }

    @Test
    fun `parses option with long name`() {
        val parser = option("some-flag").boolean()

        assertEquals(true, parser.parse("--some-flag").get())
        assertEquals(false, parser.parse().get())
    }

    @Test
    fun `parses option with multiple names`() {
        val parser = option("some-flag", "x", "a", "long").boolean()

        assertEquals(true, parser.parse("--some-flag").get())
        assertEquals(true, parser.parse("-x").get())
        assertEquals(true, parser.parse("-a").get())
        assertEquals(true, parser.parse("--long").get())
        assertEquals(false, parser.parse().get())
    }

    @Test
    fun `reports usage of option with short name`() {
        val parser = option("x").boolean()

        assertEquals("[-x]", parser.usage)
    }

    @Test
    fun `reports usage of option with long name`() {
        val parser = option("some-flag").boolean()

        assertEquals("[--some-flag]", parser.usage)
    }

    @Test
    fun `reports usage of option with multiple names`() {
        val parser = option("some-flag", "x", "a", "long").boolean()

        assertEquals("[-a | -x | --long | --some-flag]", parser.usage)
    }

    @Test
    fun `option with short name must be well formed`() {
        val parser = option("x").boolean()

        parser.fails("--x") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("-X") {
            failure(0, "Unexpected option '-X'. Try '-x' instead")
        }
        parser.fails("--X") {
            failure(0, "Unexpected option '--X'. Try '-x' instead")
        }

        parser.fails("-x", "--x") {
            failure(1, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("--x", "-x") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
    }

    @Test
    fun `option with long name must be well formed`() {
        val parser = option("some-flag").boolean()

        parser.fails("-some-flag") {
            failure(0, "Unexpected option '-some-flag'. Try '--some-flag' instead")
        }
        parser.fails("--Some-Flag") {
            failure(0, "Unexpected option '--Some-Flag'. Try '--some-flag' instead")
        }
        parser.fails("--some-flag", "-some-flag") {
            failure(1, "Unexpected option '-some-flag'. Try '--some-flag' instead")
        }
    }

    @Test
    fun `option with multiple names must be well formed`() {
        val parser = option("some-flag", "x").boolean()

        parser.fails("-some-flag") {
            failure(0, "Unexpected option '-some-flag'. Try '--some-flag' instead")
        }
        parser.fails("--Some-Flag") {
            failure(0, "Unexpected option '--Some-Flag'. Try '--some-flag' instead")
        }
        parser.fails("--x") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
        }
        parser.fails("--x", "-some-flag") {
            failure(0, "Unexpected option '--x'. Try '-x' instead")
            failure(1, "Unexpected option '-some-flag'. Try '--some-flag' instead")
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