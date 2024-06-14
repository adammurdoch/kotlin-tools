package net.rubygrapefruit.cli

import kotlin.test.*

class SubActionTest : AbstractActionTest() {
    @Test
    fun `action can have sub action with no configuration`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        parse(WithSub(), listOf("sub")) { action ->
            assertSame(sub, action.sub)
        }
    }

    @Test
    fun `action can have multiple sub actions with no configuration`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val sub by actions {
                action("s1", s1)
                action("s2", s2)
            }
        }

        parse(WithSub(), listOf("s1")) { action ->
            assertSame(s1, action.sub)
        }
        parse(WithSub(), listOf("s2")) { action ->
            assertSame(s2, action.sub)
        }
    }

    @Test
    fun `action can have sub action with configuration`() {
        class Sub : Action() {
            val flag by flag("f")
            val a by argument("a")
            val b by argument("b")
        }

        val sub = Sub()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        parse(WithSub(), listOf("sub", "--f", "1", "2")) { action ->
            assertSame(sub, action.sub)
            assertTrue(sub.flag)
            assertEquals("1", sub.a)
            assertEquals("2", sub.b)
        }
    }

    @Test
    fun `can chain sub-actions`() {
        class Sub : Action() {
            val a by argument("a")
        }

        val s1 = Sub()
        val s2 = Sub()

        class WithSub : Action() {
            val s1 by actions {
                action("s1", s1)
            }
            val s2 by actions {
                action("s2", s2)
            }
        }

        parse(WithSub(), listOf("s1", "1", "s2", "2")) { action ->
            assertSame(s1, action.s1)
            assertSame(s2, action.s2)
            assertEquals("1", s1.a)
            assertEquals("2", s2.a)
        }
    }

    @Test
    fun `fails when sub-action not provided`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        parseFails(WithSub(), emptyList(), "Action not provided")
    }

    @Test
    fun `fails when unknown sub-action provided`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        parseFails(WithSub(), listOf("thing"), "Unknown action: thing")
    }

    @Test
    fun `fails when unknown flag provided`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        parseFails(WithSub(), listOf("--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when additional args provided after action`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        parseFails(WithSub(), listOf("sub", "123"), "Unknown argument: 123")
    }

    @Test
    fun `name must not start with punctuation`() {
        class WithSub : Action() {
            val sub by actions {
                action("--sub", Action())
            }
        }
        try {
            WithSub()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--sub cannot be used as an action name", e.message)
        }
    }
}