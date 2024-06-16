package net.rubygrapefruit.cli

import kotlin.test.*

class SubActionTest : AbstractActionTest() {
    @Test
    fun `action can have sub action with no configuration`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action(sub, "sub")
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
                action(s1, "s1")
                action(s2, "s2")
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
            val a by parameter("a")
            val b by parameter("b")
        }

        class WithSub : Action() {
            val sub by actions {
                action(Sub(), "sub")
            }
        }

        parse(WithSub(), listOf("sub", "-f", "1", "2")) { action ->
            assertTrue(action.sub.flag)
            assertEquals("1", action.sub.a)
            assertEquals("2", action.sub.b)
        }
    }

    @Test
    fun `can chain sub-actions with configuration`() {
        class Sub : Action() {
            val a by parameter("a")
        }

        val s1 = Sub()
        val s2 = Sub()

        class WithSub : Action() {
            val s1 by actions {
                action(s1, "s1")
            }
            val s2 by actions {
                action(s2, "s2")
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
    fun `can nest actions`() {
        class Nested : Action() {
            val a by parameter("a")
        }

        class Sub : Action() {
            val nested by actions {
                action(Nested(), "nested")
            }
        }

        class WithSub : Action() {
            val s1 by actions {
                action(Sub(), "sub")
            }
        }

        parse(WithSub(), listOf("sub", "nested", "arg")) { action ->
            assertEquals("arg", action.s1.nested.a)
        }
    }

    @Test
    fun `fails when sub-action not provided`() {
        class WithSub : Action() {
            val sub by actions {
                action(Action(), "s1")
                action(Action(), "s2")
            }
        }

        parseFails(WithSub(), emptyList()) { e ->
            assertEquals("Action not provided", e.message)
            assertEquals(2, e.actions.size)
        }
    }

    @Test
    fun `can provide default action`() {
        val def = Action()
        val s1 = Action()

        class WithSub : Action() {
            val sub by actions {
                action(s1, "s1")
                action(Action(), "s2")
            }.whenAbsent(def)
        }

        parse(WithSub(), emptyList()) { action ->
            assertSame(def, action.sub)
        }
        parse(WithSub(), listOf("s1")) { action ->
            assertSame(s1, action.sub)
        }
    }

    @Test
    fun `fails when unknown sub-action provided`() {
        class WithSub : Action() {
            val sub by actions {
                action(Action(), "s1")
                action(Action(), "s2")
            }
        }

        parseFails(WithSub(), listOf("thing")) { e ->
            assertEquals("Unknown action: thing", e.message)
            assertEquals(2, e.actions.size)
        }
    }

    @Test
    fun `fails when unknown flag provided instead of action name`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action(sub, "sub")
            }
        }

        parseFails(WithSub(), listOf("--flag"), "Unknown option: --flag")
        parseFails(WithSub(), listOf("--flag", "sub"), "Unknown option: --flag")
        parseFails(WithSub(), listOf("sub", "--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when additional args provided after action`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action(sub, "sub")
            }
        }

        parseFails(WithSub(), listOf("sub", "123"), "Unknown parameter: 123")
    }

    @Test
    fun `name must not start with punctuation`() {
        class WithSub : Action() {
            val sub by actions {
                action(Action(), "--sub")
            }
        }
        try {
            WithSub()
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("--sub cannot be used as an action name", e.message)
        }
    }

    @Test
    fun `can run --help command without providing action`() {
        class WithSub : MainAction("cmd") {
            val sub by actions {
                action(Action(), "sub")
            }
        }

        val action = WithSub().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }
}