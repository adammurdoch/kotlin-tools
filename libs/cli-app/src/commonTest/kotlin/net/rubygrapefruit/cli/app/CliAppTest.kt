package net.rubygrapefruit.cli.app

import kotlin.test.Test
import kotlin.test.assertIs

class CliAppTest {
    @Test
    fun `can run action`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(emptyList())
        assertIs<NoConfig>(action)
    }

    @Test
    fun `can run --help action`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }

    @Test
    fun `can run --help action when unknown option is used`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(listOf("-o", "--help"))
        assertIs<HelpAction>(action)
    }

    @Test
    fun `can run --help action when unknown parameter is used`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(listOf("arg", "--help"))
        assertIs<HelpAction>(action)
    }

    @Test
    fun `can run --completion action`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(listOf("--completion"))
        assertIs<CompletionAction>(action)
    }
}