package net.rubygrapefruit.cli

import kotlin.test.*

class NestedActionOptionTest : AbstractActionTest() {
    @Test
    fun `action can have nested action with long name`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by action {
                option(sub, "sub")
            }
        }

        parse(WithSub(), listOf("--sub")) { action ->
            assertSame(sub, action.sub)
        }
        parseFails(::WithSub, listOf("-sub"), "Unknown option: -sub")
    }

    @Test
    fun `action can have nested action with short name`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by action {
                option(sub, "s")
            }
        }

        parse(WithSub(), listOf("-s")) { action ->
            assertSame(sub, action.sub)
        }
        parseFails(::WithSub, listOf("--s"), "Unknown option: --s")
    }

    @Test
    fun `action can have nested actions`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val sub by action {
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
            val sub by action {
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
            val sub by action {
                option(s1, "s1")
                option(s2, "s2")
            }
        }

        parseFails(::WithSub, emptyList(), "Action not provided")
        parseFails(::WithSub, listOf("--s2", "--s1"), "Cannot use option --s1 with option --s2")
    }

    @Test
    fun `fails when action present multiple times`() {
        val s1 = Action()
        val s2 = Action()

        class WithSub : Action() {
            val sub by action {
                option(s1, "s1")
                option(s2, "s2")
            }
        }

        parseFails(::WithSub, listOf("--s1", "--s1"), "Cannot use option --s1 multiple times")
    }

    @Test
    fun `can declare a nested action that can be used to recover from a parse error`() {
        class Sub1 : Action()

        class Sub2 : Action() {
            val p1 by parameter("p1")
            val p2 by parameter("p2")
        }

        class WithSub : Action() {
            val sub by action {
                option(Sub1(), "s1", allowAnywhere = true)
                option(Sub2(), "s2")
            }
        }

        parse(WithSub(), listOf("--s1")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("--s1", "--s1")) { action ->
            assertIs<Sub1>(action.sub)
        }

        parse(WithSub(), listOf("--s1", "--s2", "arg1", "arg2")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("--s2", "arg1", "arg2", "--s1")) { action ->
            assertIs<Sub1>(action.sub)
        }

        // Missing arguments
        parse(WithSub(), listOf("--s1", "--s2")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("--s2", "--s1")) { action ->
            assertIs<Sub1>(action.sub)
        }

        // Interleaved
        parse(WithSub(), listOf("--s2", "--s1", "arg1", "arg2")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("--s2", "arg1", "--s1", "arg2")) { action ->
            assertIs<Sub1>(action.sub)
        }

        // Unknown option
        parse(WithSub(), listOf("--s1", "--unknown", "--s2", "arg1", "arg2")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("--s1", "--unknown")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("--unknown", "--s1")) { action ->
            assertIs<Sub1>(action.sub)
        }

        // Unknown argument
        parse(WithSub(), listOf("--s1", "unknown", "--s2", "arg1", "arg2")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("--s1", "unknown")) { action ->
            assertIs<Sub1>(action.sub)
        }
        parse(WithSub(), listOf("unknown", "--s1")) { action ->
            assertIs<Sub1>(action.sub)
        }
    }

    @Test
    fun `recovery action can have parameters`() {
        class Sub1 : Action() {
            val param by parameter("param")
        }

        class Sub2 : Action() {
            val p1 by parameter("p1")
            val p2 by parameter("p2")
        }

        class WithSub : Action() {
            val sub by action {
                option(Sub1(), "s1", allowAnywhere = true)
                option(Sub2(), "s2")
            }
        }

        parse(WithSub(), listOf("--s1", "p")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("p", sub.param)
        }
        parse(WithSub(), listOf("--s1", "p", "--unknown")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("p", sub.param)
        }
        parse(WithSub(), listOf("--unknown", "--s1", "p")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("p", sub.param)
        }
        parse(WithSub(), listOf("--unknown", "--s1", "p", "unknown")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("p", sub.param)
        }
        parse(WithSub(), listOf("--s2", "--s1", "p")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("p", sub.param)
        }
        parseFails(::WithSub, listOf("--s1"), "Parameter 'param' not provided", "--s1", "<param>")
        parseFails(::WithSub, listOf("unknown", "--s1"), "Parameter 'param' not provided", "--s1", "<param>")
        parse(WithSub(), listOf("--s1", "--s1", "p")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("p", sub.param)
        }
        parse(WithSub(), listOf("--s1", "p", "--s1", "ignore")) { action ->
            val sub = action.sub
            assertIs<Sub1>(sub)
            assertEquals("p", sub.param)
        }
    }
}