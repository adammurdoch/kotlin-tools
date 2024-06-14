package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FlagAndSubActionTest : AbstractActionTest() {
    @Test
    fun `flag can precede sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by actions {
                action("sub", sub)
            }
        }

        parse(WithSub(), listOf("--flag", "sub")) { action ->
            assertTrue(action.flag)
            assertSame(sub, action.sub)
        }
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

        parse(WithSub(), listOf("sub", "--flag")) { action ->
            assertTrue(action.flag)
            assertSame(sub, action.sub)
        }
    }
}