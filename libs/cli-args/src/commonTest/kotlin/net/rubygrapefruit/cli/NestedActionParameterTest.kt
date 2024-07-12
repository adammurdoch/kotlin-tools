package net.rubygrapefruit.cli

import kotlin.test.*

class NestedActionParameterTest : AbstractActionTest() {
    @Test
    fun `action can have nested action with no configuration`() {
        val sub = Action()

        class WithSub : Action() {
            val action by actions {
                action(sub, "sub")
            }
        }

        parse(WithSub(), listOf("sub")) { action ->
            assertSame(sub, action.action)
        }
    }

    @Test
    fun `action can have nested actions with no configuration`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val action by actions {
                action(s1, "s1")
                action(s2, "s2")
            }
        }

        parse(WithSub(), listOf("s1")) { action ->
            assertSame(s1, action.action)
        }
        parse(WithSub(), listOf("s2")) { action ->
            assertSame(s2, action.action)
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
            val action by actions {
                action(Sub(), "sub")
            }
        }

        parse(WithSub(), listOf("sub", "-f", "1", "2")) { action ->
            assertTrue(action.action.flag)
            assertEquals("1", action.action.a)
            assertEquals("2", action.action.b)
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
            val action by actions {
                action(Sub1(), "s1")
                action(Sub2(), "s2")
            }
        }

        parse(WithSub(), listOf("s1", "arg")) { action ->
            val sub = action.action
            assertIs<Sub1>(sub)
            assertEquals("arg", sub.a)
        }
        parse(WithSub(), listOf("s2", "--flag")) { action ->
            val sub = action.action
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
    fun `nested action can have nested actions`() {
        class Sub1 : Action() {
            val a by parameter("a")
        }

        class Sub2 : Action() {
            val action by actions {
                action(Sub1(), "nested")
                action(Sub1(), "other")
            }
        }

        class WithSub : Action() {
            val action by actions {
                action(Sub2(), "sub")
                action(Sub2(), "other")
            }
        }

        parse(WithSub(), listOf("sub", "nested", "arg")) { action ->
            assertEquals("arg", action.action.action.a)
        }
    }

    @Test
    fun `fails when exactly one nested action not provided`() {
        class WithSub : Action() {
            val action by actions {
                action(Action(), "s1")
                action(Action(), "s2")
            }
        }

        parseFails(::WithSub, emptyList()) { e ->
            assertIs<PositionalParseException>(e)
            assertEquals("Action not provided", e.message)
            assertEquals(1, e.positional.size)
            assertIs<ActionParameterUsage>(e.positional[0])
            assertEquals(2, e.actions.size)
        }
        parseFails(::WithSub, listOf("s1", "s2"), "Unknown parameter: s2")
    }

    @Test
    fun `fails when nested action of nested action not provided`() {
        class Sub1 : Action() {
            val a by parameter("a")
        }

        class Sub2 : Action() {
            val action by actions {
                action(Sub1(), "nested")
                action(Sub1(), "other")
            }
        }

        class WithSub : Action() {
            val action by actions {
                action(Sub2(), "sub")
                action(Sub2(), "other")
            }
        }

        parseFails(::WithSub, listOf("sub")) { e ->
            assertIs<PositionalParseException>(e)
            assertEquals("Action not provided", e.message)
            assertEquals(2, e.positional.size)
            assertIs<LiteralUsage>(e.positional[0])
            assertIs<ActionParameterUsage>(e.positional[1])
        }
    }

    @Test
    fun `can provide default action`() {
        val def = Action()
        val s1 = Action()

        class WithSub : Action() {
            val action by actions {
                action(s1, "s1")
                action(Action(), "s2")
            }.whenAbsent(def)
        }

        parse(WithSub(), emptyList()) { action ->
            assertSame(def, action.action)
        }
        parse(WithSub(), listOf("s1")) { action ->
            assertSame(s1, action.action)
        }
    }

    @Test
    fun `can define optional nested actions`() {
        val s1 = Action()

        class WithSub : Action() {
            val action by actions {
                action(s1, "s1")
                action(Action(), "s2")
            }.optional()
        }

        parse(WithSub(), emptyList()) { action ->
            assertNull(action.action)
        }
        parse(WithSub(), listOf("s1")) { action ->
            assertSame(s1, action.action)
        }
    }

    @Test
    fun `fails when unknown action name provided`() {
        class WithSub : Action() {
            val action by actions {
                action(Action(), "s1")
                action(Action(), "s2")
            }
        }

        parseFails(::WithSub, listOf("thing")) { e ->
            assertIs<PositionalParseException>(e)
            assertEquals("Unknown action: thing", e.message)
            assertEquals(1, e.positional.size)
            assertIs<ActionParameterUsage>(e.positional[0])
            assertEquals(2, e.actions.size)
        }
    }

    @Test
    fun `fails when flag provided instead of action name`() {
        val sub = Action()

        class WithSub : Action() {
            val action by actions {
                action(sub, "sub")
            }
            val flag by flag("flag", "f")
        }

        parseFails(::WithSub, listOf("--flag"), "Action not provided")
        parseFails(::WithSub, listOf("-f"), "Action not provided")
        parseFails(::WithSub, listOf("--unknown"), "Unknown option: --unknown")
        parseFails(::WithSub, listOf("-u"), "Unknown option: -u")
    }

    @Test
    fun `fails when unknown flag used with action name`() {
        val sub = Action()

        class WithSub : Action() {
            val action by actions {
                action(sub, "sub")
            }
        }

        parseFails(::WithSub, listOf("--flag", "sub"), "Unknown option: --flag")
        parseFails(::WithSub, listOf("sub", "--flag"), "Unknown option: --flag")
    }

    @Test
    fun `fails when additional args provided after nested action`() {
        class WithSub : Action() {
            val action by actions {
                action(Action(), "sub")
            }
        }

        parseFails(::WithSub, listOf("sub", "123"), "Unknown parameter: 123")
    }

    @Test
    fun `can query usage of nested action by name`() {
        class WithSub : Action() {
            val action by actions {
                action(Action(), "sub")
            }
        }

        assertNotNull(WithSub().usage("sub"))
        assertNull(WithSub().usage("unknown"))
    }

    @Test
    fun `can query usage of nested action of default action by name`() {
        class Sub1 : Action() {
            val action by actions {
                action(Action(), "sub2")
            }
        }

        class WithSub : Action() {
            val action by actions {
                action(Action(), "sub")
                action(Sub1())
            }
        }

        assertNotNull(WithSub().usage("sub"))
        assertNotNull(WithSub().usage("sub2"))
        assertNull(WithSub().usage("unknown"))
    }

    @Test
    fun `name must not start with punctuation`() {
        class WithSub : Action() {
            val action by actions {
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
}