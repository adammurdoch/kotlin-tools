package net.rubygrapefruit.cli.app

import kotlin.test.Test
import kotlin.test.assertIs

class CliActionTest {
    @Test
    fun `can run --help command`() {
        class NoConfig : CliAction("cmd")

        val action = NoConfig().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }

    @Test
    fun `can run --help command when unknown option is used`() {
        class NoConfig : CliAction("cmd")

        val action = NoConfig().actionFor(listOf("-o", "--help"))
        assertIs<HelpAction>(action)
    }

    @Test
    fun `can run --help command when unknown parameter is used`() {
        class NoConfig : CliAction("cmd")

        val action = NoConfig().actionFor(listOf("arg", "--help"))
        assertIs<HelpAction>(action)
    }

    @Test
    fun `can run --completion command`() {
        class NoConfig : CliAction("cmd")

        val action = NoConfig().actionFor(listOf("--completion"))
        assertIs<CompletionAction>(action)
    }
}