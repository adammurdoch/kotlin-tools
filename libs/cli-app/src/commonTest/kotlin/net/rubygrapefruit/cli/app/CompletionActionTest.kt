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

    @Test
    fun `generates completion script for app with multiple options`() {
        class App : Action() {
            val a1 by option("some-option", help = "some other option")
            val a2 by option("s", "second-option", help = "second option")
            val a3 by option("none")
        }

        val action = CompletionAction("cmd", App())

        assertContains(action.formatted, "'--some-option[some other option]:Argument:( )'")
        assertContains(action.formatted, "{-s,--second-option}'[second option]:Argument:( )'")
        assertContains(action.formatted, "'--none:Argument:( )'")
    }
}