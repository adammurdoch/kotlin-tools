package net.rubygrapefruit.cli

import kotlin.test.*

class NestedActionOptionAndParameterTest : AbstractActionTest() {
    @Test
    fun `action can mix parameter and option nested actions`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val sub by actions {
                option(s1, "sub")
                action(s2, "sub")
            }
        }

        parse(WithSub(), listOf("--sub")) { action ->
            assertSame(s1, action.sub)
        }
        parse(WithSub(), listOf("sub")) { action ->
            assertSame(s2, action.sub)
        }
    }

    @Test
    fun `fails when exactly one action not provided`() {
        class WithSub : Action() {
            val sub by actions {
                option(Action(), "sub")
                action(Action(), "sub")
            }
        }

        parseFails(::WithSub, listOf("--sub", "sub"), "Unknown parameter: sub")
        parseFails(::WithSub, listOf("sub", "--sub"), "Unknown option: --sub")
        parseFails(::WithSub, listOf("sub", "sub"), "Unknown parameter: sub")
    }

    @Test
    fun `can define unnamed action to use when none provided`() {
        val s1 = Action()
        val s2 = Action()
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                option(s1, "sub")
                action(s2, "sub")
                action(sub)
            }
        }

        parse(WithSub(), listOf("--sub")) { action ->
            assertSame(s1, action.sub)
        }
        parse(WithSub(), listOf("sub")) { action ->
            assertSame(s2, action.sub)
        }
        parse(WithSub(), emptyList()) { action ->
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `can define unnamed action with configuration`() {
        class Sub : Action() {
            val flag by flag("flag")
            val parameter by parameter("param")
        }

        val s1 = Action()
        val s2 = Action()
        val sub = Sub()

        class WithSub : Action() {
            val sub by actions {
                option(s1, "sub")
                action(s2, "sub")
                action(sub)
            }
        }

        parse(WithSub(), listOf("--sub")) { action ->
            assertSame(s1, action.sub)
        }
        parse(WithSub(), listOf("sub")) { action ->
            assertSame(s2, action.sub)
        }
        parse(WithSub(), listOf("arg")) { action ->
            assertSame(sub, action.sub)
            assertEquals("arg", sub.parameter)
            assertFalse(sub.flag)
        }
        parse(WithSub(), listOf("--flag", "arg")) { action ->
            assertSame(sub, action.sub)
            assertEquals("arg", sub.parameter)
            assertTrue(sub.flag)
        }

        parseFails(::WithSub, emptyList(), "Parameter 'param' not provided")
        parseFails(::WithSub, listOf("--flag"), "Parameter 'param' not provided")
    }
}