package net.rubygrapefruit.cli

import kotlin.test.*

class FlagAndActionTest : AbstractActionTest() {
    @Test
    fun `can combine flag and nested action`() {
        val sub = Action()

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by action {
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
    fun `can combine flag and nested action with configuration`() {
        class Sub : Action() {
            val param by parameter("param")
        }

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by action {
                action(Sub(), "sub")
            }
        }

        parse(WithSub(), listOf("--flag", "sub", "arg")) { action ->
            assertTrue(action.flag)
            assertEquals("arg", action.sub.param)
        }

        parse(WithSub(), listOf("sub", "arg", "--flag")) { action ->
            assertTrue(action.flag)
            assertEquals("arg", action.sub.param)
        }

        parse(WithSub(), listOf("sub", "--flag", "arg")) { action ->
            assertTrue(action.flag)
            assertEquals("arg", action.sub.param)
        }

        parse(WithSub(), listOf("sub", "arg")) { action ->
            assertFalse(action.flag)
            assertEquals("arg", action.sub.param)
        }
    }

    @Test
    fun `nested action can have flag`() {
        class SubAction : Action() {
            val flag by flag("flag")
        }

        class WithSub : Action() {
            val sub by action {
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

    @Test
    fun `action and nested action with configuration can have flags`() {
        class SubAction : Action() {
            val flag by flag("inner")
            val param by parameter("param")
        }

        class WithSub : Action() {
            val flag by flag("outer")
            val sub by action {
                action(SubAction(), "sub")
            }
        }

        parse(WithSub(), listOf("sub", "--inner", "arg")) { action ->
            assertFalse(action.flag)
            assertTrue(action.sub.flag)
        }

        parse(WithSub(), listOf("sub", "--outer", "arg")) { action ->
            assertTrue(action.flag)
            assertFalse(action.sub.flag)
        }

        parse(WithSub(), listOf("sub", "--inner", "--outer", "arg")) { action ->
            assertTrue(action.flag)
            assertTrue(action.sub.flag)
        }

        parse(WithSub(), listOf("sub", "--outer", "arg", "--inner")) { action ->
            assertTrue(action.flag)
            assertTrue(action.sub.flag)
        }

        parse(WithSub(), listOf("sub", "arg")) { action ->
            assertFalse(action.flag)
            assertFalse(action.sub.flag)
        }
    }

    @Test
    fun `fails when flag follows nested action with parameter missing`() {
        class Sub : Action() {
            val param by parameter("param")
        }

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by action {
                action(Sub(), "sub")
            }
        }

        parseFails(::WithSub, listOf("sub", "--flag"), "Parameter 'param' not provided")
        parseFails(::WithSub, listOf("--flag", "sub"), "Parameter 'param' not provided")
    }
}