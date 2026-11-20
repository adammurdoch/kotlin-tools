package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class NoParametersActionTest : AbstractParseTest() {
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

    private object TestContext : Runnable.Context {
        override val output: Output
            get() = TODO("Not yet implemented")
        override val diagnostics: Output
            get() = TODO("Not yet implemented")
    }
}