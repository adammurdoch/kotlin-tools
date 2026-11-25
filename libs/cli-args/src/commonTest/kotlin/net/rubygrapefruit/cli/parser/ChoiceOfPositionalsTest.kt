package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfPositionalsTest : AbstractParseTest() {
    @Test
    fun `parses choice of int and choice positionals with overlapping values`() {
        val parser = oneOf(
            positional("a", oneOf("1" to 100, "two" to 200)),
            positional("a", unsignedInteger())
        )

        assertEquals(100, parser.parse("1").get())
        assertEquals(200, parser.parse("two").get())
        assertEquals(2, parser.parse("2").get())
    }

    @Test
    fun `argument must be well formed`() {
        val parser = oneOf(
            positional("a", oneOf("1" to 100, "two" to 200)),
            positional("a", unsignedInteger())
        )

        parser.fails("three") {
            failure(0, "Unexpected argument 'three'. Expected '1', 'two' or an unsigned int instead")
        }
    }
}