package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class NestedActionWithListParameterTest : AbstractActionTest() {
    @Test
    fun `action can consume remaining args`() {
        class Parameter : Action() {
            val param by remainder("value")
        }

        class WithSub : Action() {
            val sub by action {
                option(Parameter(), "sub")
                action(Parameter(), "sub")
            }
        }

        parse(WithSub(), listOf("--sub")) { action ->
            assertEquals(emptyList(), action.sub.param)
        }
        parse(WithSub(), listOf("sub")) { action ->
            assertEquals(emptyList(), action.sub.param)
        }
        parse(WithSub(), listOf("--sub", "a")) { action ->
            assertEquals(listOf("a"), action.sub.param)
        }
        parse(WithSub(), listOf("--sub", "a", "--flag")) { action ->
            assertEquals(listOf("a", "--flag"), action.sub.param)
        }
    }
}