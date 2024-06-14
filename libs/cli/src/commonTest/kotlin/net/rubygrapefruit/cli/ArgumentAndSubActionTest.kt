package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ArgumentAndSubActionTest : AbstractActionTest() {
    @Test
    fun `argument can precede sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val arg by argument("value")
            val sub by actions {
                action("sub", sub)
            }
        }

        parse(WithSub(), listOf("123", "sub")) { action ->
            assertEquals("123", action.arg)
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `argument can follow sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
            val arg by argument("value")
        }

        parse(WithSub(), listOf("sub", "123")) { action ->
            assertEquals("123", action.arg)
            assertSame(sub, action.sub)
        }
    }
}