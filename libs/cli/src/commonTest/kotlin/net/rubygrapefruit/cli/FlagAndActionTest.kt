package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FlagAndActionTest : AbstractActionTest() {
    @Test
    fun `can combine flag and action`() {
        val sub = Action()

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by actions {
                action(sub, "sub")
            }
        }

        parse(WithSub(), listOf("--flag", "sub")) { action ->
            assertTrue(action.flag)
            assertSame(sub, action.sub)
        }

        parse(WithSub(), listOf("sub", "--flag")) { action ->
            assertTrue(action.flag)
            assertSame(sub, action.sub)
        }

        parse(WithSub(), listOf("sub")) { action ->
            assertFalse(action.flag)
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `action can have flag`() {
        class SubAction : Action() {
            val flag by flag("flag")
        }

        class WithSub : Action() {
            val sub by actions {
                action(SubAction(), "sub")
            }
        }

        parse(WithSub(), listOf("sub", "--flag")) { action ->
            assertTrue(action.sub.flag)
        }

        parse(WithSub(), listOf("sub")) { action ->
            assertFalse(action.sub.flag)
        }
    }
}