package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NestedActionParameterAndParameterTest: AbstractActionTest() {
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

        parseFails(::WithSub, listOf("sub")) { e ->
            assertIs<PositionalParseException>(e)
            assertEquals("Parameter 'p1' not provided", e.message)
            assertEquals(3, e.positional.size)
            assertIs<LiteralUsage>(e.positional[0])
            assertIs<ParameterUsage>(e.positional[1])
            assertIs<ParameterUsage>(e.positional[2])
        }
        parseFails(::WithSub, listOf("sub", "a1")) { e ->
            assertIs<PositionalParseException>(e)
            assertEquals("Parameter 'p2' not provided", e.message)
            assertEquals(3, e.positional.size)
            assertIs<LiteralUsage>(e.positional[0])
            assertIs<ParameterUsage>(e.positional[1])
            assertIs<ParameterUsage>(e.positional[2])
        }
    }
}