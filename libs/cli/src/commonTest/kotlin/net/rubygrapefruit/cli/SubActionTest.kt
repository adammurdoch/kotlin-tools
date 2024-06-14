package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail

class SubActionTest {
    @Test
    fun `action can have sub action with no configuration`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        val action = WithSub()
        action.parse(listOf("sub"))

        assertSame(sub, action.sub)
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

        val action = WithSub()
        action.parse(listOf("s1"))

        assertSame(s1, action.sub)

        val action2 = WithSub()
        action2.parse(listOf("s2"))

        assertSame(s2, action2.sub)
    }

    @Test
    fun `fails when sub-action not provided`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        val action = WithSub()
        try {
            action.parse(emptyList())
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Command not provided", e.message)
        }
    }

    @Test
    fun `fails when unknown sub-action provided`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        val action = WithSub()
        try {
            action.parse(listOf("thing"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Unknown command 'thing'", e.message)
        }
    }

    @Test
    fun `fails when additional args provided`() {
        val sub = Action()

        class WithSub : Action() {
            val sub by actions {
                action("sub", sub)
            }
        }

        val action = WithSub()
        try {
            action.parse(listOf("sub", "123"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Unknown argument: 123", e.message)
        }
    }
}