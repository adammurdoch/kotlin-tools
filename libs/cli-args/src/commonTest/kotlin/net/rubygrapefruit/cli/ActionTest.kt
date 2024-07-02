package net.rubygrapefruit.cli

import kotlin.test.Test

class ActionTest : AbstractActionTest() {
    @Test
    fun `action can have no configuration`() {
        class NoConfig : Action()

        NoConfig().parse(emptyList())
    }

    @Test
    fun `fails when args provided for action with no configuration`() {
        class NoConfig : Action()

        parseFails(::NoConfig, listOf("--flag"), "Unknown option: --flag")
        parseFails(::NoConfig, listOf("arg"), "Unknown parameter: arg")
    }
}