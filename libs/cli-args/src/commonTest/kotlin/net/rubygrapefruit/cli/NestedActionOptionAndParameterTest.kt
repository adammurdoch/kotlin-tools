package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertSame

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

        parseFails(WithSub(), listOf("--sub", "sub"), "Unknown parameter: sub")
        parseFails(WithSub(), listOf("sub", "--sub"), "Unknown option: --sub")
        parseFails(WithSub(), listOf("sub", "sub"), "Unknown parameter: sub")
    }
}