package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ActionTest {
    @Test
    fun `action can have no configuration`() {
        class NoConfig : Action()

        NoConfig().parse(emptyList())
    }

    @Test
    fun `fails when args provided for action with no configuration`() {
        class NoConfig : Action()

        try {
            NoConfig().parse(listOf("--flag"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Unknown option: --flag", e.message)
        }

        try {
            NoConfig().parse(listOf("arg"))
            fail()
        } catch (e: ArgParseException) {
            assertEquals("Unknown argument: arg", e.message)
        }
    }
}