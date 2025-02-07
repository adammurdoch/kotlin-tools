package net.rubygrapefruit.cli

import kotlin.test.Test

class NestedActionWithParameterTest : AbstractActionTest() {
    @Test
    fun `fails when nested action parameters not provided`() {
        class Sub : Action() {
            val p1 by parameter("p1")
            val p2 by parameter("p2")
        }

        class WithSub : Action() {
            val sub by action {
                action(Sub(), "sub")
            }
        }

        parseFails(::WithSub, listOf("sub"), "Parameter 'p1' not provided", "sub", "<param>", "<param>")
        parseFails(::WithSub, listOf("sub", "a1"), "Parameter 'p2' not provided", "sub", "<param>", "<param>")
    }

    @Test
    fun `fails when additional parameters provided`() {
        class Sub : Action() {
            val p1 by parameter("p1")
            val p2 by parameter("p2")
        }

        class WithSub : Action() {
            val sub by action {
                action(Sub(), "sub")
            }
        }

        parseFails(::WithSub, listOf("sub", "a1", "a2", "b"), "Unknown parameter: b", "sub", "<param>", "<param>")
    }
}