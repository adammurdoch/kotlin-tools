package net.reubgrapefruit.process

import net.rubygrapefruit.process.Process
import kotlin.test.Test

class ProcessBuilderTest {
    @Test
    fun `can fork process`() {
        Process.start(listOf("pwd")) { }.waitFor()
        Process.start(listOf("ls")) { }.waitFor()
    }
}