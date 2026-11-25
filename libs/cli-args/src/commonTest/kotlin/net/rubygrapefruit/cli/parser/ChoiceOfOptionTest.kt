package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfOptionTest : AbstractParseTest() {
    @Test
    fun `parses choice of string and boolean option with same name`() {
        val parser = oneOf(
            option("x").required(),
            option("x").boolean().required()
        )

        assertEquals("value", parser.parse("-x", "value").get())
        assertEquals(Unit, parser.parse("-x").get())
    }

    @Test
    fun `parses choice of int and choice options with same name and overlapping values`() {
        val parser = oneOf(
            option("x", oneOf("1" to 100, "two" to 200)).required(),
            option("x", unsignedInteger()).required()
        )

        assertEquals(100, parser.parse("-x", "1").get())
        assertEquals(200, parser.parse("-x", "two").get())
        assertEquals(2, parser.parse("-x", "2").get())
        assertEquals(100, parser.parse("-x", "100").get())
    }

    @Test
    fun `reports usage of choice of string and boolean options`() {
        val parser = oneOf(
            option("x").required(),
            option("x").boolean().required()
        )

        parser.usage {
            choice("-x <value>")
            choice("-x")
        }
    }

    @Test
    fun `option cannot appear twice`() {
        val parser = oneOf(
            option("x").required(),
            option("x").boolean().required()
        )

        parser.fails("-x", "-x") {
            failure(1, "Option -x can only appear once")
        }
    }

    @Test
    fun `option must be provided`() {
        val parser = oneOf(
            option("x").required(),
            option("x").boolean().required()
        )

        parser.fails {
            expectedOption(0, "-x")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'. Expected option -x instead")
        }
    }

    @Test
    fun `option value must be well formed`() {
        val parser = oneOf(
            option("x", oneOf("1" to 100, "two" to 200)).required(),
            option("x", unsignedInteger()).required()
        )

        parser.fails("-x", "three") {
            failure(1, "Unexpected argument 'three' for option -x. Expected '1' or 'two' instead")
            failure(1, "Unexpected argument 'three' for option -x. Expected an unsigned int instead")
        }
        parser.fails("-x", "0.5") {
            failure(1, "Unexpected argument '0.5' for option -x. Expected '1' or 'two' instead")
            failure(1, "Unexpected argument '0.5' for option -x. Expected an unsigned int instead")
        }
    }
}