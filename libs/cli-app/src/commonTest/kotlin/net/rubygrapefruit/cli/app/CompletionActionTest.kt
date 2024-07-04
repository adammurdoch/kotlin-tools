package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertContains

class CompletionActionTest : AbstractActionTest() {
    @Test
    fun `generates completion script for app with no configuration`() {
        val action = CompletionAction("cmd", Action())

        assertContains(action.formatted, "compdef cmd_complete cmd")
    }
}