package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfOptionTest: AbstractParseTest() {
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
    fun `reports usage`() {
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
            // This one shouldn't appear
            failure(1, "No value provided for option -x")
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
            failure(0, "Option -x not provided")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Option -x not provided")
        }
    }
}