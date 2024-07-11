package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import kotlin.test.Test
import kotlin.test.assertContains

class CompletionActionTest : AbstractActionTest() {
    private val formatter = BufferingFormatter()

    @Test
    fun `generates completion script for app with no configuration`() {
        val action = CompletionAction("cmd", Action(), formatter)
        action.run()

        assertContains(formatter.text, "compdef cmd_complete cmd")
    }

    @Test
    fun `generates completion script for app with multiple flags`() {
        class App : Action() {
            val a1 by flag("some-flag", help = "some other flag")
            val a2 by flag("s", "second-flag", help = "second flag")
            val a3 by flag("none")
        }

        val action = CompletionAction("cmd", App(), formatter)
        action.run()

        assertContains(formatter.text, "'--some-flag[some other flag]'")
        assertContains(formatter.text, "'--no-some-flag'")
        assertContains(formatter.text, "{-s,--second-flag}'[second flag]'")
        assertContains(formatter.text, "'--no-second-flag'")
        assertContains(formatter.text, "'--none'")
        assertContains(formatter.text, "'--no-none'")
    }

    @Test
    fun `generates completion script for app with multiple options`() {
        class App : Action() {
            val a1 by option("some-option", help = "some other option")
            val a2 by option("s", "second-option", help = "second option")
            val a3 by option("none")
        }

        val action = CompletionAction("cmd", App(), formatter)
        action.run()

        assertContains(formatter.text, "'--some-option[some other option]:Argument:( )'")
        assertContains(formatter.text, "{-s,--second-option}'[second option]:Argument:( )'")
        assertContains(formatter.text, "'--none:Argument:( )'")
    }

    @Test
    fun `escapes option help text`() {
        class App : Action() {
            val a1 by option("some-option", help = "some 'option")
            val a2 by option("s", "second-option", help = "second 'option")
        }

        val action = CompletionAction("cmd", App(), formatter)
        action.run()

        assertContains(formatter.text, """'--some-option[some '"'"'option]:Argument:( )'""")
        assertContains(formatter.text, """{-s,--second-option}'[second '"'"'option]:Argument:( )'""")
    }
}