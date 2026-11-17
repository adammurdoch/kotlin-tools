package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfRequiredStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses one of string options`() {
        val parser = oneOf(
            option("a").required(),
            option("b").required(),
            option("long").required()
        )

        assertEquals("one", parser.parse("-a", "one").get())
        assertEquals("two", parser.parse("-b", "two").get())
        assertEquals("three", parser.parse("--long", "three").get())
    }

    @Test
    fun `reports usage`() {
        val parser = oneOf(
            option("a").required(),
            option("b").required(),
            option("long").required()
        )

        parser.usage {
            choice("-a <value>")
            choice("-b <value>")
            choice("--long <value>")
        }

        parser.options {
            option("-a <value>")
            option("-b <value>")
            option("--long <value>")
        }

        parser.noPositionals()
    }

    @Test
    fun `option must be provided`() {
        val parser = oneOf(
            option("a").required(),
            option("b").required(),
            option("long").required()
        )

        parser.fails {
            failure(0, "Option -a not provided")
//            failure(0, "Option -b not provided")
//            failure(0, "Option --long not provided")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Option -a not provided")
        }
    }
}