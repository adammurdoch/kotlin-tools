package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class OneCommandTest : AbstractParseTest() {
    @Test
    fun `parses command`() {
        val parser = params(command("run")) { "result" }

        assertEquals("result", parser.parse("run").get())
    }

    @Test
    fun `reports usage`() {
        val parser = params(command("run")) { fail() }

        parser.usage("run")
        parser.noOptions()
    }

    @Test
    fun `can provide summary`() {
        val parser = params(command("run"), summary = "Something useful") { fail() }

        parser.usage("run", "Something useful")
    }
}