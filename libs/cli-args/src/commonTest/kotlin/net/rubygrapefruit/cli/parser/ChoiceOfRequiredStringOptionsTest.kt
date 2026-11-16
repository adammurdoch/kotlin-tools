package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfRequiredStringOptionsTest : AbstractParseTest() {
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
}