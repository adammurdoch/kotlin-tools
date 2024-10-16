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

        assertContains(formatter.text, "compdef _cmd_complete cmd")
        assertContains(formatter.text, "function _cmd_complete() {")
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
    fun `generates completion script for app with multiple parameters`() {
        class App : Action() {
            val p1 by parameter("param1")
            val p2 by parameter("param2")
        }

        val action = CompletionAction("cmd", App(), formatter)
        action.run()

        assertContains(formatter.text, "'1:Parameter:( )'")
        assertContains(formatter.text, "'2:Parameter:( )'")
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

    @Test
    fun `generates completion script for app with actions`() {
        class Nested : Action() {
            val param by parameter("param")
        }

        class App : Action() {
            val action by actions {
                action(Action(), "a1")
                action(Nested(), "a2")
            }
        }

        val action = CompletionAction("cmd", App(), formatter)
        action.run()

        assertContains(formatter.text, "'1:Action:(a1 a2)'")
        assertContains(formatter.text, "'1:Parameter:( )'")
    }

    @Test
    fun `generates completion script for app with nested actions`() {
        class Nested1 : Action() {
            val param by parameter("param")
        }

        class Nested2: Action() {
            val action by actions {
                action(Nested1(), "n1")
                action(Action(), "n2")
            }
        }

        class App : Action() {
            val action by actions {
                action(Action(), "a1")
                action(Nested2(), "a2")
            }
        }

        val action = CompletionAction("cmd", App(), formatter)
        action.run()

        assertContains(formatter.text, "'1:Action:(a1 a2)'")
        assertContains(formatter.text, "'1:Action:(n1 n2)'")
        assertContains(formatter.text, "'1:Parameter:( )'")
    }
}