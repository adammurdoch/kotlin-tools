package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertIs

class ActionTest : AbstractActionTest() {
    @Test
    fun `action can have no configuration`() {
        class NoConfig : Action()

        NoConfig().parse(emptyList())
    }

    @Test
    fun `fails when args provided for action with no configuration`() {
        class NoConfig : Action()

        parseFails(NoConfig(), listOf("--flag"), "Unknown option: --flag")
        parseFails(NoConfig(), listOf("arg"), "Unknown argument: arg")
    }

    @Test
    fun `can run --help command`() {
        class NoConfig : MainAction("cmd")

        val action = NoConfig().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }
}