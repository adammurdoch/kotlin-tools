package net.rubygrapefruit.cli

import kotlin.test.*

class NestedActionParameterTest : AbstractActionTest() {
    @Test
    fun `action can have nested action with no configuration`() {
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
    fun `action can have nested actions with no configuration`() {
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
    fun `action can have nested action with configuration`() {
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
    fun `action can have nested actions with configuration`() {
        class Sub1 : Action() {
            val a by parameter("a")
        }

        class Sub2 : Action() {
            val f by flag("flag")
        }

        class WithSub : Action() {
            val sub by actions {
                action(Sub1(), "s1")
                action(Sub2(), "s2")
            }
        }

        parse(WithSub(), listOf("s1", "arg")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("arg", sub.a)
        }
        parse(WithSub(), listOf("s2", "--flag")) { action ->
            val sub = action.sub
            assertIs<Sub2>(sub)
            assertTrue(sub.f)
        }
    }

    @Test
    fun `action can have multiple nested actions with configuration`() {
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
    fun `can nest actions multiple levels`() {
        class Sub1 : Action() {
            val a by parameter("a")
        }

        class Sub2 : Action() {
            val action by actions {
                action(Sub1(), "nested")
            }
        }

        class WithSub : Action() {
            val action by actions {
                action(Sub2(), "sub")
            }
        }

        parse(WithSub(), listOf("sub", "nested", "arg")) { action ->
            assertEquals("arg", action.action.action.a)
        }
    }

    @Test
    fun `fails when exactly one nested action not provided`() {
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
        parseFails(WithSub(), listOf("s1", "s2"), "Unknown parameter: s2")
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
    fun `can define optional nested actions`() {
        val s1 = Action()

        class WithSub : Action() {
            val sub by actions {
                action(s1, "s1")
                action(Action(), "s2")
            }.optional()
        }

        parse(WithSub(), emptyList()) { action ->
            assertNull(action.sub)
        }
        parse(WithSub(), listOf("s1")) { action ->
            assertSame(s1, action.sub)
        }
    }

    @Test
    fun `fails when unknown action name provided`() {
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
    fun `fails when additional args provided after nested action`() {
        class WithSub : Action() {
            val sub by actions {
                action(Action(), "sub")
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
        class WithSub : TestApp() {
            val sub by actions {
                action(Action(), "sub")
            }
        }

        parseRecovers(WithSub(), listOf("--help")) { action ->
            assertTrue(action.help)
        }
    }

    @Test
    fun `can run --help command without providing action parameters`() {
        class Sub : Action() {
            val p1 by parameter("p1")
            val p2 by parameter("p2")
        }

        class WithSub : TestApp() {
            val sub by actions {
                action(Sub(), "sub")
            }
        }

        parseRecovers(WithSub(), listOf("sub", "--help")) { action ->
            assertTrue(action.help)
        }
        parseRecovers(WithSub(), listOf("sub", "a1", "--help")) { action ->
            assertTrue(action.help)
        }
        parseRecovers(WithSub(), listOf("--help", "sub", "a1")) { action ->
            assertTrue(action.help)
        }
    }
}