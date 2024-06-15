package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class StringOptionAndSubActionTest : AbstractActionTest() {
    @Test
    fun `can combine option and sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val option by option("o")
            val sub by actions {
                action(sub, "sub")
            }
        }

        parse(WithSub(), listOf("-o", "123", "sub")) { action ->
            assertEquals("123", action.option)
            assertSame(sub, action.sub)
        }

        parse(WithSub(), listOf("sub", "-o", "123")) { action ->
            assertEquals("123", action.option)
            assertSame(sub, action.sub)
        }

        parse(WithSub(), listOf("sub")) { action ->
            assertNull(action.option)
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `sub-action can have option`() {
        class SubAction : Action() {
            val option by option("o")
        }

        class WithSub : Action() {
            val sub by actions {
                action(SubAction(), "sub")
            }
        }

        parse(WithSub(), listOf("sub", "-o", "123")) { action ->
            assertEquals("123", (action.sub as SubAction).option)
        }

        parse(WithSub(), listOf("sub")) { action ->
            assertNull((action.sub as SubAction).option)
        }
    }
}