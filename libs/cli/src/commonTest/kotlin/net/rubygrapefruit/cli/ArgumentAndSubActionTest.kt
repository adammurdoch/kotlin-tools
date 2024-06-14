package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ArgumentAndSubActionTest {
    @Test
    fun `argument can precede sub-action`() {
        val sub = Action()

        class WithSub : Action() {
            val arg by argument("value")
            val sub by actions {
                action("sub", sub)
            }
        }

        val action = WithSub()
        action.parse(listOf("123", "sub"))

        assertEquals("123", action.arg)
        assertSame(sub, action.sub)
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

        val action = WithSub()
        action.parse(listOf("sub", "123"))

        assertEquals("123", action.arg)
        assertSame(sub, action.sub)
    }
}