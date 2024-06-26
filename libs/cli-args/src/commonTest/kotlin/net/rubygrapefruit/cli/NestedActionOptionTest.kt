package net.rubygrapefruit.cli

import kotlin.test.*

class NestedActionOptionTest : AbstractActionTest() {
    @Test
    fun `action can have nested action with long name`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                option(sub, "sub")
            }
        }

        parse(WithSub(), listOf("--sub")) { action ->
            assertSame(sub, action.sub)
        }
        parseFails(WithSub(), listOf("-sub"), "Unknown option: -sub")
    }

    @Test
    fun `action can have nested action with short name`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                option(sub, "s")
            }
        }

        parse(WithSub(), listOf("-s")) { action ->
            assertSame(sub, action.sub)
        }
        parseFails(WithSub(), listOf("--s"), "Unknown option: --s")
    }

    @Test
    fun `action can have nested actions`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val sub by actions {
                option(s1, "s1")
                option(s2, "s2")
            }
        }

        parse(WithSub(), listOf("--s1")) { action ->
            assertSame(s1, action.sub)
        }
        parse(WithSub(), listOf("--s2")) { action ->
            assertSame(s2, action.sub)
        }
    }

    @Test
    fun `action can have nested actions with configuration`() {
        class Sub1 : Action() {
            val param by parameter("param")
            val flag by flag("flag")
        }

        class Sub2 : Action() {
            val p1 by parameter("p1")
            val p2 by parameter("p2")
        }

        class WithSub : Action() {
            val sub by actions {
                option(Sub1(), "s1")
                option(Sub2(), "s2")
            }
        }

        parse(WithSub(), listOf("--s1", "arg", "--flag")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("arg", sub.param)
            assertTrue(sub.flag)
        }
        parse(WithSub(), listOf("--s2", "arg1", "arg2")) { action ->
            val sub = action.sub
            assertIs<Sub2>(sub)
            assertEquals("arg1", sub.p1)
            assertEquals("arg2", sub.p2)
        }
    }

    @Test
    fun `fails when exactly one nested action not provided`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val sub by actions {
                option(s1, "s1")
                option(s2, "s2")
            }
        }

        parseFails(WithSub(), emptyList(), "Action not provided")
        parseFails(WithSub(), listOf("--s2", "--s1"), "Unknown option: --s1")
    }
}