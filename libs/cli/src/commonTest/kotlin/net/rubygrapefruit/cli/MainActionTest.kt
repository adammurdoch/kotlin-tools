package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertIs

class MainActionTest : AbstractActionTest() {
    @Test
    fun `can run --help command`() {
        class NoConfig : MainAction("cmd")

        val action = NoConfig().actionFor(listOf("--help"))
        assertIs<HelpAction>(action)
    }
}