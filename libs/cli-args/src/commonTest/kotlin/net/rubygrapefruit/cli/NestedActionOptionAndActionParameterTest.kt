package net.rubygrapefruit.cli

import kotlin.test.*

class NestedActionOptionAndActionParameterTest : AbstractActionTest() {
    @Test
    fun `action can mix parameter and option nested actions`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val sub by action {
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
        class Parameter: Action() {
            val param by parameter("param")
        }
        class WithSub : Action() {
            val sub by action {
                option(Parameter(), "sub")
                action(Parameter(), "sub")
                action(Parameter(), "sub2")
            }
        }

        parseFails(::WithSub, emptyList(), "Action not provided")
        parseFails(::WithSub, listOf("--sub", "p", "sub"), "Cannot use action 'sub' with option --sub")
        parseFails(::WithSub, listOf("sub", "--sub"), "Cannot use option --sub with action 'sub'")
        parseFails(::WithSub, listOf("sub", "p", "--sub"), "Cannot use option --sub with action 'sub'")
        parseFails(::WithSub, listOf("sub", "p", "sub2"), "Cannot use action 'sub2' with action 'sub'")
    }

    @Test
    fun `fails when action present multiple times`() {
        class WithSub : Action() {
            val sub by action {
                option(Action(), "sub")
                action(Action(), "sub")
            }
        }

        parseFails(::WithSub, listOf("sub", "sub"), "Cannot use action 'sub' multiple times")
        parseFails(::WithSub, listOf("--sub", "--sub"), "Cannot use option --sub multiple times")
    }

    @Test
    fun `fails when exactly one action not provided and unnamed action specified`() {
        class Sub : Action() {
            val param by parameter("param")
        }

        class WithSub : Action() {
            val sub by action {
                option(Action(), "sub")
                action(Sub())
            }
        }

        parseFails(::WithSub, listOf("unknown", "sub"), "Unknown parameter: sub")
        parseFails(::WithSub, listOf("sub", "unknown"), "Unknown parameter: unknown")
        parseFails(::WithSub, listOf("sub", "sub"), "Unknown parameter: sub")
    }

    @Test
    fun `can define unnamed action to use when none provided`() {
        val s1 = Action()
        val s2 = Action()
        val sub = Action()

        class WithSub : Action() {
            val sub by action {
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
            val sub by action {
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