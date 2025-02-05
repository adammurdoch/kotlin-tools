package net.rubygrapefruit.cli

import kotlin.test.Test

class NestedActionWithOptionTest : AbstractActionTest() {
    @Test
    fun `fails when action option provided multiple times`() {
        class Option : Action() {
            val option1 by option("o")
            val option2 by option("p")
        }

        class WithSub : Action() {
            val sub by action {
                option(Option(), "sub")
                action(Option(), "sub")
            }
        }

        parseFails(::WithSub, listOf("sub", "-o", "1", "-o", "2"), "Value for option -o already provided")
        parseFails(::WithSub, listOf("--sub", "-o", "1", "-o", "2"), "Value for option -o already provided")
        parseFails(::WithSub, listOf("sub", "-o", "1", "-p", "2", "-o", "3"), "Value for option -o already provided")
        parseFails(::WithSub, listOf("sub", "-o", "1", "-p", "2", "-o"), "Value for option -o already provided")
    }
}