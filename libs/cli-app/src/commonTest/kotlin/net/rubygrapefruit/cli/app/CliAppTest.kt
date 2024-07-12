package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertIs

class CliAppTest {
    private val formatter = BufferingFormatter()

    @Test
    fun `can run action`() {
        class App : CliApp("cmd")

        val action = App().actionFor(emptyList(), formatter)
        assertIs<App>(action)
    }

    @Test
    fun `can run --help action`() {
        class App : CliApp("cmd")

        val action = App().actionFor(listOf("--help"), formatter)
        assertIs<HelpAction>(action)

        action.run()

        assertContains(formatter.text, "--help")
        assertContains(formatter.text, "--stack")
        assertContains(formatter.text, "--completion")
    }

    @Test
    fun `can run --help action and help action when action has nested actions`() {
        class App : CliApp("cmd") {
            val action by actions {
                action(Action(), "action")
            }
        }

        for (arg in listOf("--help", "help")) {
            val formatter = BufferingFormatter()
            val action = App().actionFor(listOf(arg, "action"), formatter)
            assertIs<NestedActionHelpAction>(action)

            action.run()

            assertContains(formatter.text, "Usage: cmd action")
        }
    }

    @Test
    fun `can run --help action when unknown option is used`() {
        class App : CliApp("cmd")

        val action1 = App().actionFor(listOf("-o", "--help"), formatter)
        assertIs<HelpAction>(action1)
        action1.run()

        val action2 = App().actionFor(listOf("--help", "--o"), formatter)
        assertIs<HelpAction>(action2)
        action1.run()
    }

    @Test
    fun `can run --help action when unknown parameter is used`() {
        class App : CliApp("cmd")

        val action1 = App().actionFor(listOf("arg", "--help"), formatter)
        assertIs<HelpAction>(action1)
        action1.run()

        val action2 = App().actionFor(listOf("--help", "arg"), formatter)
        assertIs<HelpAction>(action2)
        action2.run()
    }

    @Test
    fun `can run --completion action`() {
        class App : CliApp("cmd")

        val action = App().actionFor(listOf("--completion"), formatter)
        assertIs<CompletionAction>(action)
        action.run()
    }
}