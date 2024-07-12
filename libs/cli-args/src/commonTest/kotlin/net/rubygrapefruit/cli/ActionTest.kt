package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertNull

class ActionTest : AbstractActionTest() {
    @Test
    fun `action can have no configuration`() {
        class NoConfig : Action()

        parse(::NoConfig, emptyList()) { _ -> }
    }

    @Test
    fun `fails when args provided for action with no configuration`() {
        class NoConfig : Action()

        parseFails(::NoConfig, listOf("--flag"), "Unknown option: --flag")
        parseFails(::NoConfig, listOf("arg"), "Unknown parameter: arg")
    }

    @Test
    fun `nested action usage query returns null for action with no configuration`() {
        class NoConfig : Action()

        assertNull(NoConfig().usage("name"))
    }
}