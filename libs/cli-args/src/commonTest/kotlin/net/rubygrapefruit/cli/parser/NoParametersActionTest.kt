package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class NoParametersActionTest : AbstractActionParseTest() {
    @Test
    fun `parses no parameters`() {
        var done = false
        val parser = action {
            done = true
        }

        parser.parse().get().run {
            TestContext.run()
        }
        assertTrue(done)
    }

    @Test
    fun `reports usage`() {
        val parser = action { fail() }

        parser.usage("")
        parser.noOptions()
        parser.noPositionals()
    }

    @Test
    fun `can provide summary`() {
        val parser = action("Something useful") { fail() }

        parser.usage("", "Something useful")
        parser.noOptions()
        parser.noPositionals()
    }
}