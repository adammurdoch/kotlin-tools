package net.rubygrapefruit.cli

import kotlin.test.*

class ParameterAndActionTest : AbstractActionTest() {
    @Test
    fun `parameter can precede action with no configuration`() {
        val sub = Action()

        class WithSub : Action() {
            val param by parameter("value")
            val sub by action {
                action(sub, "sub")
            }
        }

        parse(WithSub(), listOf("123", "sub")) { action ->
            assertEquals("123", action.param)
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `parameter can follow action with no configuration`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by action {
                action(sub, "sub")
            }
            val param by parameter("value")
        }

        parse(WithSub(), listOf("sub", "123")) { action ->
            assertEquals("123", action.param)
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `parameter can follow action with configuration`() {
        class Sub : Action() {
            val f by flag("flag")
            val p by parameter("param")
        }

        class WithSub : Action() {
            val sub by action {
                action(Sub(), "sub")
            }
            val param by parameter("value")
        }

        parse(WithSub(), listOf("sub", "arg", "123")) { action ->
            assertEquals("123", action.param)
            assertEquals("arg", action.sub.p)
            assertFalse(action.sub.f)
        }
        parse(WithSub(), listOf("sub", "arg", "--flag", "123")) { action ->
            assertEquals("123", action.param)
            assertEquals("arg", action.sub.p)
            assertTrue(action.sub.f)
        }
    }
}