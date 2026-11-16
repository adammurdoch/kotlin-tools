package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class RequiredStringOptionTest : AbstractParseTest() {
    @Test
    fun `parses option with short name`() {
        val parser = option("x").required()

        assertEquals("value", parser.parse("-x", "value").get())
        assertEquals("", parser.parse("-x", "").get())
    }

    @Test
    fun `parses option with long name`() {
        val parser = option("some-option").required()

        assertEquals("value", parser.parse("--some-option", "value").get())
    }

    @Test
    fun `parses option with multiple names`() {
        val parser = option("some-option", "x").required()

        assertEquals("value", parser.parse("--some-option", "value").get())
        assertEquals("value", parser.parse("-x", "value").get())
    }

    @Test
    fun `reports usage of option with short name`() {
        val parser = option("x").required()

        parser.usage("-x <value>")
        parser.option("-x <value>")
        parser.noPositionals()
    }

    @Test
    fun `can provide summary`() {
        val parser = option("x", summary = "Something useful").required()

        parser.usage("-x <value>")
        parser.option("-x <value>", "Something useful")
        parser.noPositionals()
    }

    @Test
    fun `can provide value name`() {
        val parser = option("x", valueName = "thing").required()

        parser.usage("-x <thing>")
        parser.option("-x <thing>")
        parser.noPositionals()
    }

    @Test
    fun `option must be present`() {
        val parser = option("some-flag", "x").required()

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
    fun `option with multiple names cannot appear twice`() {
        val parser = option("long", "x", "an-option").required()

        parser.fails("-x", "value", "-x", "value") {
            failure(2, "Option -x can only appear once")
        }
        parser.fails("-x", "value", "--long", "value") {
            failure(2, "Option --long cannot be used with -x")
        }
        parser.fails("--an-option", "value", "--long", "value") {
            failure(2, "Option --long cannot be used with --an-option")
        }
    }
}