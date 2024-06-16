package net.rubygrapefruit.cli

import kotlin.test.*

class FlagAndActionTest : AbstractActionTest() {
    @Test
    fun `can combine flag and nested action`() {
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
    fun `can combine flag and nested action with configuration`() {
        class Sub : Action() {
            val param by parameter("param")
        }

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by actions {
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

    @Test
    fun `fails when nested action parameter is missing`() {
        class Sub : Action() {
            val param by parameter("param")
        }

        class WithSub : Action() {
            val flag by flag("flag")
            val sub by actions {
                action(Sub(), "sub")
            }
        }

        parseFails(WithSub(), listOf("sub", "--flag"), "Parameter 'param' not provided")
        parseFails(WithSub(), listOf("--flag", "sub"), "Parameter 'param' not provided")
    }
}