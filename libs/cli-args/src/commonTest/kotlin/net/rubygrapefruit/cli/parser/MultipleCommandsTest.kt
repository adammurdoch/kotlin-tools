package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MultipleCommandsTest : AbstractParseTest() {
    @Test
    fun `parses two commands`() {
        val parser = params(command("one"), command("two")) { "result" }

        assertEquals("result", parser.parse("one", "two").get())
    }

    @Test
    fun `reports usage`() {
        val parser = params(command("one"), command("two")) { fail() }

        parser.usage("one two")
        parser.noOptions()
        parser.noPositionals()
    }

    @Test
    fun `can provide summary`() {
        val parser = params(command("one"), command("two"), summary = "Something useful") { fail() }

        parser.usage("one two", "Something useful")
    }
}