package net.reubgrapefruit.process

import net.rubygrapefruit.process.Process
import kotlin.test.Test

class ProcessBuilderTest {
    @Test
    fun `can fork process`() {
        val process = Process.start(listOf("pwd")) { }
        process.waitFor()
    }
}