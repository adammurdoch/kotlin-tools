package net.reubgrapefruit.process

import net.rubygrapefruit.process.Process
import kotlin.test.Test
import kotlin.test.assertTrue

class ProcessBuilderTest {
    @Test
    fun `can fork process`() {
        Process.start(listOf("pwd")).waitFor()
        Process.start(listOf("ls")).waitFor()
    }

    @Test
    fun `can fork process and collect output`() {
        val pwd = Process.command(listOf("pwd")).startAndCollectOutput().waitFor()
        println("-> pwd output: $pwd")
        assertTrue(pwd.isNotEmpty())

        val ls = Process.command(listOf("ls")).startAndCollectOutput().waitFor()
        println("-> ls output: $ls")
        assertTrue(ls.isNotEmpty())
    }
}