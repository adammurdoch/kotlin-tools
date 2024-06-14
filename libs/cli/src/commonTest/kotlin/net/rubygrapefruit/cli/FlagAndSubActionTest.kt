package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FlagAndSubActionTest {
    @Test
    fun `flag can precede sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by actions {
                action("sub", sub)
            }
        }

        val action = WithSub()
        action.parse(listOf("--flag", "sub"))

        assertTrue(action.flag)
        assertSame(sub, action.sub)
    }

    @Test
    fun `flag can follow sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by actions {
                action("sub", sub)
            }
        }

        val action = WithSub()
        action.parse(listOf("sub", "--flag"))

        assertTrue(action.flag)
        assertSame(sub, action.sub)
    }
}