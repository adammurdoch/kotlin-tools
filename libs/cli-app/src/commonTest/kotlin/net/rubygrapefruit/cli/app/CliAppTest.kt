package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CliAppTest {
    private val formatter = BufferingFormatter()

    @Test
    fun `can run action`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(emptyList(), formatter)
        assertIs<NoConfig>(action)
    }

    @Test
    fun `can run --help action`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(listOf("--help"), formatter)
        assertIs<HelpAction>(action)

        action.run()

        assertContains(formatter.text, "--help")
        assertContains(formatter.text, "--stack")
        assertContains(formatter.text, "--completion")
    }

    @Test
    fun `can run --help action when unknown option is used`() {
        class NoConfig : CliApp("cmd")

        val action1 = NoConfig().actionFor(listOf("-o", "--help"), formatter)
        assertIs<HelpAction>(action1)

        val action2 = NoConfig().actionFor(listOf("--help", "--o"), formatter)
        assertIs<HelpAction>(action2)
    }

    @Test
    fun `can run --help action when unknown parameter is used`() {
        class NoConfig : CliApp("cmd")

        val action1 = NoConfig().actionFor(listOf("arg", "--help"), formatter)
        assertIs<HelpAction>(action1)

        val action2 = NoConfig().actionFor(listOf("--help", "arg"), formatter)
        assertIs<HelpAction>(action2)
    }

    @Test
    fun `can run --completion action`() {
        class NoConfig : CliApp("cmd")

        val action = NoConfig().actionFor(listOf("--completion"), formatter)
        assertIs<CompletionAction>(action)
    }
}