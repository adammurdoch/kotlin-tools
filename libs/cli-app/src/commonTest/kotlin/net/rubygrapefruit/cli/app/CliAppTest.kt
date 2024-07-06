package net.rubygrapefruit.cli.app

import kotlin.test.Test
import kotlin.test.assertContains
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

        val formatter = BufferingFormatter()
        action.run(formatter)

        assertContains(formatter.text, "--help")
        assertContains(formatter.text, "--stack")
        assertContains(formatter.text, "--completion")
    }

    @Test
    fun `can run --help action when unknown option is used`() {
        class NoConfig : CliApp("cmd")

        val action1 = NoConfig().actionFor(listOf("-o", "--help"))
        assertIs<HelpAction>(action1)

        val action2 = NoConfig().actionFor(listOf("--help", "--o"))
        assertIs<HelpAction>(action2)
    }

    @Test
    fun `can run --help action when unknown parameter is used`() {
        class NoConfig : CliApp("cmd")

        val action1 = NoConfig().actionFor(listOf("arg", "--help"))
        assertIs<HelpAction>(action1)

        val action2 = NoConfig().actionFor(listOf("--help", "arg"))
        assertIs<HelpAction>(action2)
    }

    @Test
    fun `can run --completion action`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(listOf("--completion"))
        assertIs<CompletionAction>(action)
    }
}