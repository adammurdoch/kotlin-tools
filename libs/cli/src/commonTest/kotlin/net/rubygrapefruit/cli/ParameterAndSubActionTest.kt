package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ParameterAndSubActionTest : AbstractActionTest() {
    @Test
    fun `parameter can precede sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val param by parameter("value")
            val sub by actions {
                action("sub", sub)
            }
        }

        parse(WithSub(), listOf("123", "sub")) { action ->
            assertEquals("123", action.param)
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `parameter can follow sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
            val param by parameter("value")
        }

        parse(WithSub(), listOf("sub", "123")) { action ->
            assertEquals("123", action.param)
            assertSame(sub, action.sub)
        }
    }
}